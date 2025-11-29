package com.teekasarthi.teekasarthi.supervisor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorDTO {
    private String employeeId;
    private String name;
    private String phone;
}
