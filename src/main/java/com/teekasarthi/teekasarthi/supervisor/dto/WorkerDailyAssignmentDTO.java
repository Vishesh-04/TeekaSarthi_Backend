package com.teekasarthi.teekasarthi.supervisor.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor // Generates the constructor for all fields
@NoArgsConstructor
public class WorkerDailyAssignmentDTO {
    private Long id;
    private LocalDate date;
    private Integer assigned;
    private Integer completed;
    private WorkerDTO worker;
    private CenterDTO center;
    private List<AssignmentTaskDTO> tasks; // This field is required
}