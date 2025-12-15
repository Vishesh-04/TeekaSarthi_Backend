package com.teekasarthi.teekasarthi.supervisor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerTaskDTO {
    private Long taskId;
    private String taskStatus; // e.g., PENDING, VACCINATED
    private Long beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryPhone; // Add other beneficiary fields as needed
}