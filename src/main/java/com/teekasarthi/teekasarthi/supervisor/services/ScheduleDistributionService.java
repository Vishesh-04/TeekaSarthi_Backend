package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.entity.Beneficiary;
import com.teekasarthi.teekasarthi.repository.BeneficiaryRepo;
import com.teekasarthi.teekasarthi.supervisor.entity.LoadOverflow;
import com.teekasarthi.teekasarthi.supervisor.entity.Worker;
import com.teekasarthi.teekasarthi.supervisor.entity.WorkerDailyAssignment;
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

    private static final int CAPACITY_PER_WORKER =10;

    private final WorkerRepository workerRepository;
    private final LoadOverflowRepository loadOverflowRepository;
    private final WorkerDailyAssignmentRepository workerDailyAssignmentRepository;
    private final BeneficiaryRepo beneficiaryRepo;
    private  final CenterRepository centerRepository;

    public int computeLoadForCenter(Long centerId, LocalDate date) {
        Long due = beneficiaryRepo.countByCenterIdAndNextDueDate(centerId, date);
        int totalDue = due.intValue();
        int overflow = loadOverflowRepository.findByCenter_IdAndDate(centerId, date)
                .map(LoadOverflow::getLeftover)
                .orElse(0);
        return totalDue + overflow;

    }

    @Transactional
    public void distributeForCenterAndDate(Long centerId, LocalDate date) {
        log.info("Starting distribution for center={} date={}", centerId, date);

        var centerOpt = centerRepository.findById(centerId);
        if (centerOpt.isEmpty()) {
            log.warn("Center {} not found, aborting distribution", centerId);
            return;
        }
        int load = computeLoadForCenter(centerId, date);
        log.info("Computed load = {} for center {}", load, centerId);
        List<Worker> workers = workerRepository.findByCenterId(centerId)
                .stream()
                .filter(w -> Boolean.TRUE.equals(w.getActive()))
                .sorted(Comparator.comparingLong(Worker::getId))
                .collect(Collectors.toList());

        if ((workers.isEmpty())) {
            log.warn("No active workers found for center {}, cannot distribute load", centerId);
            saveOverflow(centerId, date.plusDays(1), load);
            return;
        }

        List<WorkerDailyAssignment> existing = workerDailyAssignmentRepository
                .findByCenter_IdAndDate(centerId, date);
        if (!existing.isEmpty()) {
            workerDailyAssignmentRepository.deleteAll(existing);
            log.info("Assignments already exist for center={} date={}, skipping distribution", centerId, date);
        }

        int n = workers.size();

        // base and remainder
        int base = load / n;
        int remainder = load % n;

        // prepare assignments
        List<WorkerDailyAssignment> assignments = new ArrayList<>();
        Map<Long, Integer> assignedMap = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            Worker w = workers.get(i);
            int tentative = base + (i < remainder ? 1 : 0);
            assignedMap.put(w.getId(), tentative);
        }

        int totalAssigned = assignedMap.values().stream().mapToInt(Integer::intValue).sum();
        log.info("Total assigned = {}, Load = {}", totalAssigned, load);

        int remaining = 0;
        for (Worker w : workers) {
            int val = assignedMap.get(w.getId());
            if (val > CAPACITY_PER_WORKER) {
                remaining += (val - CAPACITY_PER_WORKER);
                assignedMap.put(w.getId(), CAPACITY_PER_WORKER);
            }
        }
        totalAssigned = assignedMap.values().stream().mapToInt(Integer::intValue).sum();
        remaining += (load - totalAssigned);
        log.info("After capacity check, total assigned = {}, Remaining = {}", totalAssigned, remaining);

        boolean redistributed = true;
        while(remaining > 0 && redistributed) {
            redistributed = false;
            List<Long> withSpare = workers.stream()
                    .filter(w -> assignedMap.get(w.getId()) < CAPACITY_PER_WORKER)
                    .map(Worker::getId)
                    .collect(Collectors.toList());

            if (withSpare.isEmpty()) break;

            int m = withSpare.size();
            int per = remaining / m;
            int rem2 = remaining % m;

            if(per==0 && rem2==0) break;

            for(int i =0; i< withSpare.size() && remaining > 0; i++) {
                Long wid = withSpare.get(i);
                int spare = CAPACITY_PER_WORKER - assignedMap.get(wid);
                int add = per + (i < rem2 ? 1 : 0);
                if (add <= 0) continue;
                int delta = Math.min(spare, add);
                if (delta > 0) {
                    assignedMap.put(wid, assignedMap.get(wid) + delta);
                    remaining -= delta;
                    redistributed = true;
                }
            }

            }

            int leftoverForNextDay = Math.min(0, remaining);

        for (Worker w : workers) {
            int assigned = assignedMap.get(w.getId());
            WorkerDailyAssignment assignment = WorkerDailyAssignment.builder()
                    .worker(w)
                    .center(centerOpt.get())
                    .date(date)
                    .assigned(assigned)
                    .completed(0)
                    .build();
            assignments.add(assignment);
        }
        workerDailyAssignmentRepository.saveAll(assignments);
        log.info("Saved {} assignments for center={} date={}", assignments.size(), centerId, date);
        if (leftoverForNextDay > 0) {
            saveOverflow(centerId, date.plusDays(1), leftoverForNextDay);
            log.info("Leftover for center={} date={}", centerId, date);
        }else {
            log.info("No leftover for center={} date={}", centerId, date);
        }

        }
    private void saveOverflow(Long centerId, LocalDate date, int leftover) {
        if (leftover <= 0) return;
        // merge with existing overflow for that date (if any)
        LoadOverflow overflow = loadOverflowRepository.findByCenter_IdAndDate(centerId, date)
                .orElse(LoadOverflow.builder().center(centerRepository.getReferenceById(centerId)).date(date).leftover(0).build());
        overflow.setLeftover(overflow.getLeftover() + leftover);
        loadOverflowRepository.save(overflow);
    }


    }



















