package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.entity.Beneficiary;
import com.teekasarthi.teekasarthi.repository.BeneficiaryRepo;
import com.teekasarthi.teekasarthi.supervisor.entity.*;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.LoadOverflowRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerDailyAssignmentRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleDistributionService {

    // Hard limit per worker
    private static final int CAPACITY_PER_WORKER = 10;

    private final WorkerRepository workerRepository;
    private final LoadOverflowRepository loadOverflowRepository;
    private final WorkerDailyAssignmentRepository workerDailyAssignmentRepository;
    private final BeneficiaryRepo beneficiaryRepo;
    private final CenterRepository centerRepository;

    public int computeLoadForCenter(Long centerId, LocalDate date) {
        Long due = beneficiaryRepo.countByCenterIdAndNextDueDate(centerId, date);
        int totalDue = due != null ? due.intValue() : 0;

        // Check if there was overflow from the previous day meant for today
        int overflowFromYesterday = loadOverflowRepository.findByCenter_IdAndDate(centerId, date)
                .map(LoadOverflow::getLeftover)
                .orElse(0);

        return totalDue + overflowFromYesterday;
    }

    @Transactional
    public void distributeForCenterAndDate(Long centerId, LocalDate date) {
        log.info("Starting ENTITY distribution for center={} date={}", centerId, date);

        Center center = centerRepository.findById(centerId).orElse(null);
        if (center == null) {
            log.warn("Center {} not found, aborting distribution", centerId);
            return;
        }

        // 1. Fetch Actual Beneficiaries (The Workload)
        // We need the actual entities now, not just the count.
        List<Beneficiary> allBeneficiaries = beneficiaryRepo.findAllByCenterIdAndNextDueDateAndVerifiedTrue(centerId, date);        int totalLoad = allBeneficiaries.size();
        log.info("Found {} beneficiaries due for center {}", totalLoad, centerId);

        // Note: We are ignoring 'LoadOverflow' table for fetching here because
        // we need actual Beneficiary entities to create AssignmentTasks.
        // If "Overflow" means "People from yesterday", they should be queried via date logic.

        if (totalLoad == 0) {
            log.info("No beneficiaries to distribute for center {}", centerId);
            return;
        }

        // 2. Get Active Workers
        List<Worker> workers = workerRepository.findByCenterId(centerId)
                .stream()
                .filter(w -> Boolean.TRUE.equals(w.getActive()))
                .sorted(Comparator.comparingLong(Worker::getId))
                .collect(Collectors.toList());

        // 3. Handle No Workers case
        if (workers.isEmpty()) {
            log.warn("No active workers found for center {}, pushing stats to overflow", centerId);
            // We cannot assign tasks if no workers, so we just log the overflow count
            saveOverflow(center, date.plusDays(1), totalLoad);
            return;
        }

        // 4. Clean existing assignments for this day
        List<WorkerDailyAssignment> existing = workerDailyAssignmentRepository.findByCenter_IdAndDate(centerId, date);
        if (!existing.isEmpty()) {
            log.info("Found {} existing assignments. Overwriting.", existing.size());
            // JPA CascadeType.ALL on 'tasks' will automatically delete the child AssignmentTasks
            workerDailyAssignmentRepository.deleteAll(existing);
            workerDailyAssignmentRepository.flush();
        }

        // 5. Calculate Capacity & Slice the List
        int numWorkers = workers.size();
        int totalCapacity = numWorkers * CAPACITY_PER_WORKER;

        List<Beneficiary> beneficiariesToAssign;
        int overflowCount = 0;

        if (totalLoad > totalCapacity) {
            // Take only as many as we can fit
            beneficiariesToAssign = allBeneficiaries.subList(0, totalCapacity);
            // The rest are overflow
            overflowCount = totalLoad - totalCapacity;
        } else {
            // We can fit everyone
            beneficiariesToAssign = allBeneficiaries;
            overflowCount = 0;
        }

        // 6. Distribute the "beneficiariesToAssign" list among workers
        int assignableCount = beneficiariesToAssign.size();
        int baseAssignment = assignableCount / numWorkers;
        int remainder = assignableCount % numWorkers;

        // "Cursor" to track which beneficiary we are currently picking from the list
        int beneficiaryCursor = 0;

        List<WorkerDailyAssignment> newAssignments = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = workers.get(i);

            // Calculate how many tasks THIS worker gets
            // Workers at index 0 to remainder-1 get +1 task
            int countForThisWorker = baseAssignment + (i < remainder ? 1 : 0);

            // Skip if logic results in 0 (unlikely if load > workers)
            if (countForThisWorker == 0) continue;

            // Create the Parent Assignment
            WorkerDailyAssignment assignment = WorkerDailyAssignment.builder()
                    .worker(worker)
                    .center(center)
                    .date(date)
                    .assigned(countForThisWorker)
                    .completed(0)
                    .tasks(new ArrayList<>()) // Initialize the list
                    .build();

            // Create the Child Tasks (AssignmentTask)
            for (int j = 0; j < countForThisWorker; j++) {
                // Get the next beneficiary from the list
                Beneficiary b = beneficiariesToAssign.get(beneficiaryCursor++);

                AssignmentTask task = AssignmentTask.builder()
                        .assignment(assignment) // Link Parent
                        .beneficiary(b)         // Link Beneficiary
                        .status("PENDING")      // Default status
                        .build();

                assignment.getTasks().add(task);
            }

            newAssignments.add(assignment);
        }

        // Saving assignments will cascade and save all AssignmentTasks
        workerDailyAssignmentRepository.saveAll(newAssignments);
        log.info("Successfully created assignments for {} workers with specific tasks.", newAssignments.size());

        // 7. Handle Overflow
        if (overflowCount > 0) {
            log.info("Capacity exceeded by {}. Saving to overflow stats.", overflowCount);
            // We record the number of people we couldn't assign
            saveOverflow(center, date.plusDays(1), overflowCount);
        } else {
            log.info("All due beneficiaries assigned. No overflow.");
        }
    }

    private void saveOverflow(Center center, LocalDate date, int amount) {
        if (amount <= 0) return;

        LoadOverflow overflow = loadOverflowRepository.findByCenter_IdAndDate(center.getId(), date)
                .orElse(LoadOverflow.builder()
                        .center(center)
                        .date(date)
                        .leftover(0)
                        .build());

        overflow.setLeftover(overflow.getLeftover() + amount);
        loadOverflowRepository.save(overflow);
    }
}