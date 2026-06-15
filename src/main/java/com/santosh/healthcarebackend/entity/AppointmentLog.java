package com.santosh.healthcarebackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment_logs")
public class AppointmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus newStatus;

    private String message;

    private LocalDateTime changedAt = LocalDateTime.now();
}