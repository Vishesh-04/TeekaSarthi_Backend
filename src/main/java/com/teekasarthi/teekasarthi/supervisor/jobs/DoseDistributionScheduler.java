package com.teekasarthi.teekasarthi.supervisor.jobs;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import com.teekasarthi.teekasarthi.supervisor.services.ScheduleDistributionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoseDistributionScheduler {

    private final ScheduleDistributionService distributionService;
    private final CenterRepository centerRepository;

    // Runs every day at 00:10 AM (Midnight + 10 mins)
    @Scheduled(cron = "0 52 13 * * *", zone = "Asia/Kolkata")
    public void runDistributionForAllCenters() {
        log.info("Running scheduled dose distribution job");

        List<Center> centers = centerRepository.findAll();
        LocalDate targetDate = LocalDate.now();

        for (Center c : centers) {
            try {
                distributionService.distributeForCenterAndDate(c.getId(), targetDate);
            } catch (Exception e) {
                log.error("Failed to distribute for center {}: {}", c.getId(), e.getMessage(), e);
            }
        }

        log.info("Distribution job completed for: {}", targetDate);
    }
    @PostConstruct
    public void test() {
        System.out.println("Scheduler bean initialized!");
    }


}