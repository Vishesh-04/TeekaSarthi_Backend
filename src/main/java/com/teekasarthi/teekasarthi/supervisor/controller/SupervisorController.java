package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.ApiResponse;
import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import com.teekasarthi.teekasarthi.supervisor.services.SupervisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
@Slf4j
public class SupervisorController {

    private final SupervisorService supervisorService;

    @GetMapping("/center/{centerId}/tasks")
    public ResponseEntity<?> getCenterTasks(@PathVariable Long centerId) {

        List<AssignmentTask> tasks = supervisorService.getTasksByCenter(centerId);

        // Wrap response in a structure compatible with your React code: res.data.data
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", tasks);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/supervisor/{supervisorId}/centers
     */
    @GetMapping("/{supervisorId}/centers")
    public ResponseEntity<ApiResponse> getCenters(@PathVariable Long supervisorId) {
        log.debug("GET centers for supervisor {}", supervisorId);
        var centers = supervisorService.getCentersForSupervisor(supervisorId);
        return ResponseEntity.ok(new ApiResponse(true, "Centers fetched", centers));
    }

    /**
     * GET /api/supervisor/center/{centerId}/workers
     */
    @GetMapping("/center/{centerId}/workers")
    public ResponseEntity<ApiResponse> getWorkers(@PathVariable Long centerId) {
        log.debug("GET workers for center {}", centerId);
        var workers = supervisorService.getWorkersForCenter(centerId);
        return ResponseEntity.ok(new ApiResponse(true, "Workers fetched", workers));
    }
}