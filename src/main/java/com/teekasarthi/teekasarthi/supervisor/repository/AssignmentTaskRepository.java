package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.AssignmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask, Long> {

    @Query("SELECT t FROM AssignmentTask t " +
            "JOIN FETCH t.beneficiary b " +
            "WHERE t.assignment.worker.id = :workerId " +
            "AND t.assignment.date = :date " +
            "AND t.status = 'PENDING'") // 👈 Added this line
    List<AssignmentTask> findPendingTasks(@Param("workerId") Long workerId, @Param("date") LocalDate date);

    @Query("SELECT t FROM AssignmentTask t " +
                "JOIN FETCH t.beneficiary b " +    // Already fetching beneficiary
                "JOIN FETCH t.assignment a " +     // NEW: Fetch assignment eagerly
                "JOIN FETCH a.worker w " +         // NEW: Fetch worker eagerly
                "WHERE w.center.id = :centerId")
    List<AssignmentTask> findTasksByCenterId(@Param("centerId") Long centerId);
    Optional<AssignmentTask> findByAssignmentIdAndBeneficiaryId(Long assignmentId, Long beneficiaryId);

    Optional<AssignmentTask> findByAssignmentId(Long assignmentId);
}