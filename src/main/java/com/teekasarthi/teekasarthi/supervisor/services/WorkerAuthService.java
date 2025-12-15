package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.dto.LoginResponse;
import com.teekasarthi.teekasarthi.supervisor.dto.WorkerDTO;
import com.teekasarthi.teekasarthi.supervisor.entity.Worker;
import com.teekasarthi.teekasarthi.supervisor.repository.WorkerAuthRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkerAuthService {

    private final WorkerAuthRepository workerRepo;

    public WorkerAuthService(WorkerAuthRepository workerRepo) {
        this.workerRepo = workerRepo;
    }

    public LoginResponse login(WorkerDTO dto) {

        Worker worker = workerRepo.findByEmployeeCode(dto.getEmployeeCode());

        if (worker == null) {
            return new LoginResponse(false, "Invalid Employee Code");
        }

        if (!worker.getName().equalsIgnoreCase(dto.getName())) {
            return new LoginResponse(false, "Name does not match");
        }

        if (!worker.getPhone().equals(dto.getPhone())) {
            return new LoginResponse(false, "Phone number does not match");
        }

        return new LoginResponse(true, "Worker login successful");
    }
}

