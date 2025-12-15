package com.teekasarthi.teekasarthi.supervisor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.teekasarthi.teekasarthi.entity.Beneficiary;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Parent Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    @JsonBackReference // Prevent recursion
    private WorkerDailyAssignment assignment;

    // Link to the Beneficiary (Read-only reference mostly)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private Beneficiary beneficiary;

    // Track status for THIS specific task (e.g., "PENDING", "VACCINATED", "ABSENT")
    @Builder.Default
    private String status = "PENDING";
}