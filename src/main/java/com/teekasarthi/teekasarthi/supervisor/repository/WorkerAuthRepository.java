package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerAuthRepository extends JpaRepository<Worker, Long> {

    Worker findByEmployeeCode(String employeeCode);
}
