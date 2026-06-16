package com.santosh.healthcarebackend.repository;

import com.santosh.healthcarebackend.entity.Appointment;
import com.santosh.healthcarebackend.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    boolean existsByDoctorNameAndAppointmentTimeAndStatusNot(
            String doctorName, LocalDateTime appointmentTime, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctorName = :doctorName AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorAndTimeRange(String doctorName, LocalDateTime start, LocalDateTime end);
}