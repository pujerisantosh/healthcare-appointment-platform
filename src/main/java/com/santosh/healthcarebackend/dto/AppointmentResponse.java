package com.santosh.healthcarebackend.dto;

import com.santosh.healthcarebackend.entity.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;
    private String doctorName;
    private String specialization;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private String patientName;
    private LocalDateTime createdAt;
}