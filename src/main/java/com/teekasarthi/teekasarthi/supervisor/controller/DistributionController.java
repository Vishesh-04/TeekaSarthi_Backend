package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.ApiResponse;
import com.teekasarthi.teekasarthi.supervisor.dto.CenterDTO;
import com.teekasarthi.teekasarthi.supervisor.dto.WorkerDTO;
import com.teekasarthi.teekasarthi.supervisor.dto.WorkerDailyAssignmentDTO;
import com.teekasarthi.teekasarthi.supervisor.entity.WorkerDailyAssignment;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerDailyAssignmentRepository;
import com.teekasarthi.teekasarthi.supervisor.services.ScheduleDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/supervisor/distribution")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:3000"})
public class DistributionController {

    private final ScheduleDistributionService distributionService;
    private final WorkerDailyAssignmentRepository assignmentRepository;

    private WorkerDailyAssignmentDTO toDTO(WorkerDailyAssignment a) {
        WorkerDTO workerDTO = new WorkerDTO(
                a.getWorker().getId(),
                a.getWorker().getName(),
                a.getWorker().getPhone(),
                a.getWorker().getEmployeeCode()
        );

        CenterDTO centerDTO = new CenterDTO(
                a.getCenter().getId(),
                a.getCenter().getCode()
        );

        return new WorkerDailyAssignmentDTO(
                a.getId(),
                a.getDate(),
                a.getAssigned(),
                a.getCompleted(),
                workerDTO,
                centerDTO
        );
    }

    // Manual trigger to distribute for a given center and date
    @PostMapping("/distribute/{centerId}")
    public ResponseEntity<ApiResponse> distribute(
            @PathVariable Long centerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate target = (date == null) ? LocalDate.now() : date;
        distributionService.distributeForCenterAndDate(centerId, target);
        return ResponseEntity.ok(new ApiResponse(true, "Distribution completed for center " + centerId + " date " + target, null));
    }

    // Supervisor view: list assignments for a center and date
    @GetMapping("/center/{centerId}")
    public ResponseEntity<ApiResponse> getAssignmentsForCenter(
            @PathVariable Long centerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate target = (date == null) ? LocalDate.now() : date;
        List<WorkerDailyAssignment> list = assignmentRepository.findByCenter_IdAndDate(centerId, target);
        return ResponseEntity.ok(
                new ApiResponse(true, "Assignments fetched",
                        list.stream().map(this::toDTO).toList()
                )
        );
    }

    // Worker view: get assignment for worker for today (or provided date)
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<ApiResponse> getAssignmentForWorker(
            @PathVariable Long workerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate target = (date == null) ? LocalDate.now() : date;
        List<WorkerDailyAssignment> list = assignmentRepository.findByWorker_IdAndDate(workerId, target);
        return ResponseEntity.ok(
                new ApiResponse(true, "Worker assignment fetched",
                        list.stream().map(this::toDTO).toList()
                )
        );
    }
}
