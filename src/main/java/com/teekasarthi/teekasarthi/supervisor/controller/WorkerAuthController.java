package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.LoginResponse;
import com.teekasarthi.teekasarthi.supervisor.dto.WorkerDTO;
import com.teekasarthi.teekasarthi.supervisor.services.WorkerAuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/worker/auth")
@CrossOrigin("http://localhost:5173")
public class WorkerAuthController {

    private final WorkerAuthService authService;

    public WorkerAuthController(WorkerAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody WorkerDTO dto) {
        return authService.login(dto);
    }
}
