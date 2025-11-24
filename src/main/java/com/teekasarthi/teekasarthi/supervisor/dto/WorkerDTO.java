package com.teekasarthi.teekasarthi.supervisor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO {
    private Long id;
    private String name;
    private String phone;
    private String employeeCode;
}
