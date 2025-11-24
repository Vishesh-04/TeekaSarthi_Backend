package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.WorkerTaskDTO;
import com.teekasarthi.teekasarthi.supervisor.services.AssignmentTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/supervisor/assignments")
@RequiredArgsConstructor
public class AssignmentTaskController {

    private final AssignmentTaskService assignmentTaskService;

    @GetMapping("/worker/{workerId}/beneficiaries")
    public ResponseEntity<List<WorkerTaskDTO>> getBeneficiariesByWorker(
            @PathVariable Long workerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<WorkerTaskDTO> response = assignmentTaskService.getBeneficiariesForWorker(workerId, date);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/task/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status) {

        // Simple validation (Optional: move to Service)
        if (!status.equals("VACCINATED") && !status.equals("ABSENT") && !status.equals("PENDING")) {
            return ResponseEntity.badRequest().build();
        }

        assignmentTaskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok().build();
    }
}