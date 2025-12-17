package com.teekasarthi.teekasarthi.supervisor.dto;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorLoginResponse {
    private boolean success;
    private String message;

    // Supervisor Data fields
    private int id;
    private String name;
    private String employeeId;
    private String email;
    private String phoneNumber;

    // Relationship
    private List<Center> centers;
}
