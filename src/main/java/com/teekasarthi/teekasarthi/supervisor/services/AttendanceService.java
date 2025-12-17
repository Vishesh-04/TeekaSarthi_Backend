package com.teekasarthi.teekasarthi.supervisor.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.teekasarthi.teekasarthi.entity.Beneficiary;
import com.teekasarthi.teekasarthi.repository.BeneficiaryRepo;
import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import com.teekasarthi.teekasarthi.supervisor.entity.WorkerDailyAssignment;
import com.teekasarthi.teekasarthi.supervisor.repository.AssignmentTaskRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerDailyAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final Cloudinary cloudinary;
    private final WorkerDailyAssignmentRepository workerDailyAssignmentRepository;
    private final BeneficiaryRepo beneficiaryRepo;
    private final AssignmentTaskRepository assignmentTaskRepository;

    @Transactional
    public void processBulkAttendance(
            Long workerId,
            Long centerId,
            String location, // This maps to submissionAddress
            String latitude, // 👇 Added Parameter
            String longitude, // 👇 Added Parameter
            Long assignmentId,
            Map<Long, MultipartFile> photos,
            Map<Long, String> nextDueDates
    ) {
        // Track how many *new* tasks are completed to update the counters correctly
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        photos.forEach((beneficiaryId, file) -> {
            try {
                // 1. Upload Photo to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String photoUrl = (String) uploadResult.get("secure_url");

                // 2. Update Beneficiary Details (Next Due Date)
                Beneficiary beneficiary = beneficiaryRepo.findById(beneficiaryId)
                        .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + beneficiaryId));

                if (nextDueDates.containsKey(beneficiaryId)) {
                    String dateStr = nextDueDates.get(beneficiaryId);
                    if (dateStr != null && !dateStr.isEmpty()) {
                        beneficiary.setNextDueDate(LocalDate.parse(dateStr));
                    }
                }
                beneficiaryRepo.save(beneficiary);

                // 3. Update Assignment Task
                AssignmentTask task = assignmentTaskRepository.findByAssignmentIdAndBeneficiaryId(assignmentId, beneficiaryId)
                        .orElseThrow(() -> new RuntimeException("Task not found for Assignment " + assignmentId));

                // --- SAVE PROOF & LOCATION ---
                task.setProofPhotoUrl(photoUrl);

                // 👇 Save the location coordinates where the proof was submitted
                task.setSubmissionLatitude(latitude);
                task.setSubmissionLongitude(longitude);
                task.setSubmissionAddress(location); // using the 'location' string as the address

                // 4. Update Status & Counts (Only if not already successful)
                if (!"SUCCESS".equals(task.getStatus())) {
                    task.setStatus("SUCCESS");
                    successfulUpdates.incrementAndGet();
                }

                // Save the task
                assignmentTaskRepository.save(task);

            } catch (Exception e) {
                System.err.println("Error for beneficiary " + beneficiaryId + ": " + e.getMessage());
                throw new RuntimeException("Partial failure: " + e.getMessage());
            }
        });

        // --- STEP 5: Update Parent Assignment Counts ---
        if (successfulUpdates.get() > 0) {
            WorkerDailyAssignment assignment = workerDailyAssignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

            // Decrement Assigned
            int currentAssigned = assignment.getAssigned();
            int newAssigned = Math.max(0, currentAssigned - successfulUpdates.get());
            assignment.setAssigned(newAssigned);

            // Increment Completed
            int currentCompleted = assignment.getCompleted();
            int newCompleted = currentCompleted + successfulUpdates.get();
            assignment.setCompleted(newCompleted);

            workerDailyAssignmentRepository.save(assignment);
        }
    }
}