package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.dto.SupervisorDTO;
import com.teekasarthi.teekasarthi.supervisor.entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisorAuthRep extends JpaRepository<Supervisor, Long> {
    Supervisor findByEmployeeId(String EmployeeId);
}
