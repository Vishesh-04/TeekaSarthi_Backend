package com.teekasarthi.teekasarthi.supervisor.repository;


import com.teekasarthi.teekasarthi.supervisor.dto.WorkerCenterDto;
import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long> {

    // Returns all centers that belong to a supervisor (pagination-ready)
    Page<Center> findBySupervisorId(Long supervisorId, Pageable pageable);

    // Simple non-paged convenience method
    List<Center> findBySupervisorId(Long supervisorId);
    @Query("""
        SELECT new com.teekasarthi.teekasarthi.supervisor.dto.WorkerCenterDto(
            c.id, 
            c.name, 
            w.id
        ) 
        FROM Worker w 
        JOIN w.center c 
        WHERE w.employeeCode = :employeeCode
    """)
    Optional<WorkerCenterDto> findWorkerAndCenterDetails(@Param("employeeCode") String employeeCode);
}
