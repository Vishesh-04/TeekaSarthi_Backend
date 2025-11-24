package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.LoadOverflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadOverflowRepository extends JpaRepository<LoadOverflow, Long> {
    Optional<LoadOverflow> findByCenter_IdAndDate(Long centerId, LocalDate date);
}
