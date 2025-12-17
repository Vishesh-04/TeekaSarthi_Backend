package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByWorkerId(Long workerId);
    List<Attendance> findByCenterId(Long centerId);
}