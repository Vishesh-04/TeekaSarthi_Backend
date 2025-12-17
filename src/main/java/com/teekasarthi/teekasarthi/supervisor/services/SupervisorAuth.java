package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorDTO;
import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorLoginResponse;
import com.teekasarthi.teekasarthi.supervisor.entity.Supervisor;
import com.teekasarthi.teekasarthi.supervisor.repository.SupervisorAuthRep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

@Service
public class SupervisorAuth {

    private final SupervisorAuthRep supervisorAuthRep;

    public SupervisorAuth(SupervisorAuthRep supervisorAuthRep) {
        this.supervisorAuthRep = supervisorAuthRep;
    }

    @Transactional // Keeps session open to fetch the list of centers
    public SupervisorLoginResponse login(SupervisorDTO supervisorDTO) {

        Supervisor supervisor = supervisorAuthRep.findByEmployeeId(supervisorDTO.getEmployeeId());

        // 1. Check if user exists
        if (supervisor == null) {
            return SupervisorLoginResponse.builder()
                    .success(false)
                    .message("Employee ID not found")
                    .build();
        }

        // 2. Validate Name
        if (!supervisor.getName().equalsIgnoreCase(supervisorDTO.getName())) {
            return SupervisorLoginResponse.builder()
                    .success(false)
                    .message("Name does not match")
                    .build();
        }

        // 3. Validate Phone
        if (!supervisor.getPhoneNumber().equals(supervisorDTO.getPhone())) {
            return SupervisorLoginResponse.builder()
                    .success(false)
                    .message("Phone number does not match")
                    .build();
        }

        // 4. Success - Return all details
        return SupervisorLoginResponse.builder()
                .success(true)
                .message("Login successful")
                .id(supervisor.getId())
                .name(supervisor.getName())
                .employeeId(supervisor.getEmployeeId())
                .email(supervisor.getEmail())
                .phoneNumber(supervisor.getPhoneNumber())
                .centers(supervisor.getCenters()) // Fetching lazy loaded list
                .build();
    }
}