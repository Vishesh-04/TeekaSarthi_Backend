package com.teekasarthi.teekasarthi.supervisor.repository;


import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long> {

    // Returns all centers that belong to a supervisor (pagination-ready)
    Page<Center> findBySupervisorId(Long supervisorId, Pageable pageable);

    // Simple non-paged convenience method
    List<Center> findBySupervisorId(Long supervisorId);
}
