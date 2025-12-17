package com.teekasarthi.teekasarthi.supervisor.dto;

public record WorkerCenterDto(
        Long centerId,
        String centerName,
        Long workerId  // This is the Database Primary Key (ID), not the Employee Code
) {}