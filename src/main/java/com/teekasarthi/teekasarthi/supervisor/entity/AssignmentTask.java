package com.teekasarthi.teekasarthi.supervisor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(name = "proof_photo_url")
    private String proofPhotoUrl;

    @JsonProperty("workerName")
    public String getWorkerName() {
        if (assignment != null && assignment.getWorker() != null) {
            return assignment.getWorker().getName();
        }
        return "Unknown";
    }

    // Getters and Setters...
    public void setProofPhotoUrl(String proofPhotoUrl) {
        this.proofPhotoUrl = proofPhotoUrl;
    }
    public String getProofPhotoUrl() {
        return proofPhotoUrl;
    }
}