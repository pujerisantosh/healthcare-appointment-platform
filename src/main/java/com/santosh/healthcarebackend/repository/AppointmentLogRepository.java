package com.santosh.healthcarebackend.repository;

import com.santosh.healthcarebackend.entity.AppointmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentLogRepository extends JpaRepository<AppointmentLog, Long> {
    List<AppointmentLog> findByAppointmentIdOrderByChangedAtDesc(Long appointmentId);
}