package com.teekasarthi.teekasarthi.supervisor.repository;

import com.teekasarthi.teekasarthi.supervisor.entity.VaccinationSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VaccinationSummaryRepository extends JpaRepository<VaccinationSummary, Long> {

    // Find summaries by center
    List<VaccinationSummary> findByCenterId(Long centerId);

    // Find summaries by supervisor id
    List<VaccinationSummary> findBySupervisorId(Long supervisorId);

    // Custom query to find by supervisor and year — joins are avoided because supervisorId is denormalized field.
    List<VaccinationSummary> findBySupervisorIdAndYear(Long supervisorId, Integer year);

    // Aggregate for a month-year for a supervisor: use a custom query to sum doses per center (optional)
    @Query("SELECT v.center.id, SUM(v.dosesAdministered) FROM VaccinationSummary v WHERE v.supervisorId = :supervisorId AND v.year = :year AND v.month = :month GROUP BY v.center.id")
    List<Object[]> sumDosesByCenterForSupervisorAndMonth(Long supervisorId, Integer month, Integer year);
}
