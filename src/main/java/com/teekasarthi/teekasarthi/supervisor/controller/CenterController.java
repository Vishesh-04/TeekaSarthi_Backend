package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.WorkerCenterDto;
import com.teekasarthi.teekasarthi.supervisor.services.CenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/center")
@RequiredArgsConstructor
public class CenterController {
    private final CenterService centerService;
    @GetMapping("/worker/{employeeCode}")
    public ResponseEntity<WorkerCenterDto> getCenterByEmployee(@PathVariable String employeeCode) {
        WorkerCenterDto dto = centerService.getWorkerCenterDetails(employeeCode);
        return ResponseEntity.ok(dto);
    }
}
