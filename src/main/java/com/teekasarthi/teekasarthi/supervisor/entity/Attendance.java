package com.teekasarthi.teekasarthi.supervisor.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workerId;
    private Long centerId;
    private String latitude;
    private String longitude;
    private String location;

    // Optional: Link to the specific task for traceability
    private Long assignmentTaskId;

    private String imageUrl; // Optional: Stores GPS string

    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}