package com.teekasarthi.teekasarthi.supervisor.jobs;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import com.teekasarthi.teekasarthi.supervisor.services.ScheduleDistributionService;
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

    @Scheduled(cron = "0 10 0 * * *")
    public void runDistributionForAllCenters() {
        log.info("Running nightly dose distribution job");
        List<Center> centers = centerRepository.findAll();
        LocalDate target = LocalDate.now().plusDays(1); // distribute for next day (so workers see assignments in morning)
        for (Center c : centers) {
            try {
                distributionService.distributeForCenterAndDate(c.getId(), target);
            } catch (Exception e) {
                log.error("Failed to distribute for center {}: {}", c.getId(), e.getMessage(), e);
            }
        }
    }
}
