package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask, Long> {

    @Query("SELECT t FROM AssignmentTask t " +
            "JOIN FETCH t.beneficiary b " +
            "WHERE t.assignment.worker.id = :workerId " +
            "AND t.assignment.date = :date")
    List<AssignmentTask> findTasksByWorkerAndDate(@Param("workerId") Long workerId,
                                                  @Param("date") LocalDate date);
}