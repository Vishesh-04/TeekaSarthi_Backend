package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    // Workers in a center (pagination-ready)
    Page<Worker> findByCenterId(Long centerId, Pageable pageable);

    List<Worker> findByCenterId(Long centerId);
}
