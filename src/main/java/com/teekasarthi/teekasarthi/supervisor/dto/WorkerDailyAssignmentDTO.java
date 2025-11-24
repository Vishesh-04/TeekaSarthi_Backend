package com.teekasarthi.teekasarthi.supervisor.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDailyAssignmentDTO {
    private Long id;
    private LocalDate date;
    private Integer assigned;
    private Integer completed;

    private WorkerDTO worker;
    private CenterDTO center;
}
