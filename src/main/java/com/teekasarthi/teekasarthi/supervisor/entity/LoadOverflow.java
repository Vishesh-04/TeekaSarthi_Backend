package com.teekasarthi.teekasarthi.supervisor.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "load_overflow",
        uniqueConstraints = @UniqueConstraint(columnNames = {"center_id","date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadOverflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "leftover", nullable = false)
    private Integer leftover = 0;
}