package com.teekasarthi.teekasarthi.supervisor.controller;

import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorDTO;
import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorLoginResponse;
import com.teekasarthi.teekasarthi.supervisor.services.SupervisorAuth;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor/auth")
public class SupervisorAuthController {

    private final SupervisorAuth supervisorAuth;

    public SupervisorAuthController(SupervisorAuth supervisorAuth) {
        this.supervisorAuth = supervisorAuth;
    }

    @PostMapping("/login")
    public SupervisorLoginResponse login(@RequestBody SupervisorDTO dto) {
        return supervisorAuth.login(dto);
    }
}

