package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorDTO;
import com.teekasarthi.teekasarthi.supervisor.dto.LoginResponse;
import com.teekasarthi.teekasarthi.supervisor.entity.Supervisor;
import com.teekasarthi.teekasarthi.supervisor.repository.SupervisorAuthRep;
import org.springframework.stereotype.Service;

@Service
public class SupervisorAuth {

    private final SupervisorAuthRep supervisorAuthRep;

    public SupervisorAuth(SupervisorAuthRep supervisorAuthRep) {
        this.supervisorAuthRep = supervisorAuthRep;
    }

    public LoginResponse login(SupervisorDTO supervisorDTO) {

        Supervisor supervisor = supervisorAuthRep.findByEmployeeId(supervisorDTO.getEmployeeId());

        if (supervisor == null) {
            return new LoginResponse(false, "Employee ID not found");
        }

        if (!supervisor.getName().equalsIgnoreCase(supervisorDTO.getName())) {
            return new LoginResponse(false, "Name does not match");
        }

        if (!supervisor.getPhoneNumber().equals(supervisorDTO.getPhone())) {
            return new LoginResponse(false, "Phone number does not match");
        }

        return new LoginResponse(true, "Login successful");
    }
}
