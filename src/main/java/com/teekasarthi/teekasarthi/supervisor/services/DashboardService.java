package com.teekasarthi.teekasarthi.supervisor.services;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import com.teekasarthi.teekasarthi.supervisor.entity.VaccinationSummary;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
import com.teekasarthi.teekasarthi.supervisor.repository.VaccinationSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard service prepares aggregated data for supervisor dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final CenterRepository centerRepository;
    private final VaccinationSummaryRepository vaccinationSummaryRepository;

    /**
     * Returns list of centers under a supervisor.
     */
    public List<Center> centersUnderSupervisor(Long supervisorId) {
        log.debug("Listing centers under supervisor {}", supervisorId);
        return centerRepository.findBySupervisorId(supervisorId);
    }

    /**
     * Returns a map representing vaccination summary for a given month and year.
     * Example output:
     * {
     *   "month": "JANUARY",
     *   "year": 2025,
     *   "totalDoses": 1200,
     *   "byCenter": {
     *      1: 400,
     *      2: 800
     *   }
     * }
     */
    public Map<String, Object> monthlyVaccinationMap(Long supervisorId, Integer month, Integer year) {
        log.debug("Building monthly vaccination map for supervisor={}, month={}, year={}", supervisorId, month, year);
        Map<String, Object> result = new HashMap<>();
        List<VaccinationSummary> summaries = vaccinationSummaryRepository.findBySupervisorIdAndYear(supervisorId, year)
                .stream()
                .filter(v -> Objects.equals(v.getMonth(), month))
                .collect(Collectors.toList());

        int totalDoses = summaries.stream().mapToInt(v -> Optional.ofNullable(v.getDosesAdministered()).orElse(0)).sum();
        int totalBeneficiaries = summaries.stream().mapToInt(v -> Optional.ofNullable(v.getBeneficiaries()).orElse(0)).sum();

        Map<Long, Integer> byCenter = summaries.stream()
                .collect(Collectors.groupingBy(v -> v.getCenter().getId(),
                        Collectors.summingInt(v -> Optional.ofNullable(v.getDosesAdministered()).orElse(0))));

        result.put("month", Month.of(month).name());
        result.put("year", year);
        result.put("totalDoses", totalDoses);
        result.put("totalBeneficiaries", totalBeneficiaries);
        result.put("byCenter", byCenter);

        return result;
    }
}