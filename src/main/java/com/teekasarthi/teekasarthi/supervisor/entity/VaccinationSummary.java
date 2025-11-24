package com.teekasarthi.teekasarthi.supervisor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "vaccination_summaries",
        indexes = {
                @Index(name = "idx_center_date", columnList = "center_id, date"),
                @Index(name = "idx_supervisor_year", columnList = "supervisor_id, year")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Integer year;
    private Integer month;
    private Integer dosesAdministered = 0;
    private Integer beneficiaries = 0;

    // Denormalized field
    @Column(name = "supervisor_id")
    private Long supervisorId;

    // 👇 Link to Center (avoid deep recursion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    @JsonIgnore // don't serialize full center to avoid recursion or heavy JSON
    private Center center;

    // 👇 Link to Worker (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    @JsonIgnore // same reason: keep lightweight JSON
    private Worker worker;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
