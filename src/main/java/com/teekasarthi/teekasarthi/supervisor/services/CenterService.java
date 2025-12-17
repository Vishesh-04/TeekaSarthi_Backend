package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.dto.WorkerCenterDto;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CenterService {
    @Autowired
    public CenterRepository  centerRepository;
    public WorkerCenterDto getWorkerCenterDetails(String employeeCode) {
        return centerRepository.findWorkerAndCenterDetails(employeeCode)
                .orElseThrow(() -> new RuntimeException("Worker or Center not found for code: " + employeeCode));
    }
}
