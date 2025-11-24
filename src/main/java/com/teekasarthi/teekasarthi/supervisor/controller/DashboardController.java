package com.teekasarthi.teekasarthi.supervisor.controller;


import com.teekasarthi.teekasarthi.supervisor.dto.ApiResponse;
import com.teekasarthi.teekasarthi.supervisor.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/supervisor/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/supervisor/dashboard/{supervisorId}/monthly/{month}/{year}
     */
    @GetMapping("/{supervisorId}/monthly/{month}/{year}")
    public ResponseEntity<ApiResponse> getMonthlySummary(@PathVariable Long supervisorId,
                                                         @PathVariable Integer month,
                                                         @PathVariable Integer year) {
        log.debug("GET monthly dashboard for supervisor={}, month={}, year={}", supervisorId, month, year);
        var map = dashboardService.monthlyVaccinationMap(supervisorId, month, year);
        return ResponseEntity.ok(new ApiResponse(true, "Monthly vaccination summary", map));
    }
}