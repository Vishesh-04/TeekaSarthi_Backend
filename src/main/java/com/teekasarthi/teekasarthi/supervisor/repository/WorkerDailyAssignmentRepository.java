package com.teekasarthi.teekasarthi.supervisor.repository;


import com.teekasarthi.teekasarthi.supervisor.entity.WorkerDailyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkerDailyAssignmentRepository extends JpaRepository<WorkerDailyAssignment, Long> {
    List<WorkerDailyAssignment> findByCenter_IdAndDate(Long centerId, LocalDate date);

    // Fetch assignments for a worker on a date
    List<WorkerDailyAssignment> findByWorker_IdAndDate(Long workerId, LocalDate date);

    // Fetch single assignment for worker+date
    Optional<WorkerDailyAssignment> findOneByWorker_IdAndDate(Long workerId, LocalDate date);
}
