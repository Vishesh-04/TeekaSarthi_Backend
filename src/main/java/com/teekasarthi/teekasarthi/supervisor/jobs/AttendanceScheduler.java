package com.teekasarthi.teekasarthi.supervisor.jobs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for auto attendance job.
 * Runs nightly (cron configured in application.yml as teekasarthi.scheduler.attendance-cron)
 * The cron below is a default; in production move cron to configuration.
 */
@Slf4j
@Component
public class AttendanceScheduler {
    @Scheduled(cron = "0 10 0 * * *")
    public void runAutoAttendance() {
        log.info("Running auto-attendance job for supervisors module");
        // TODO: implement attendance logic: mark attendance, create records, notify if required
    }
}