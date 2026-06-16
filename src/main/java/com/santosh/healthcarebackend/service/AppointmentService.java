package com.santosh.healthcarebackend.service;

import com.santosh.healthcarebackend.dto.AppointmentRequest;
import com.santosh.healthcarebackend.dto.AppointmentResponse;
import com.santosh.healthcarebackend.entity.Appointment;
import com.santosh.healthcarebackend.entity.AppointmentLog;
import com.santosh.healthcarebackend.entity.AppointmentStatus;
import com.santosh.healthcarebackend.entity.User;
import com.santosh.healthcarebackend.kafka.AppointmentEventProducer;
import com.santosh.healthcarebackend.repository.AppointmentLogRepository;
import com.santosh.healthcarebackend.repository.AppointmentRepository;
import com.santosh.healthcarebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentLogRepository appointmentLogRepository;
    private final UserRepository userRepository;
    private final AppointmentEventProducer eventProducer;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isDuplicate = appointmentRepository
                .existsByDoctorNameAndAppointmentTimeAndStatusNot(
                        request.getDoctorName(),
                        request.getAppointmentTime(),
                        AppointmentStatus.CANCELLED);
        if (isDuplicate) {
            throw new RuntimeException("Slot already booked for this doctor at the given time");
        }

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctorName(request.getDoctorName());
        appointment.setSpecialization(request.getSpecialization());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        logStatusChange(appointment, null, AppointmentStatus.CONFIRMED, "Appointment booked");

        eventProducer.sendBookingEvent(String.format(
                "{\"appointmentId\":%d,\"patient\":\"%s\",\"doctor\":\"%s\",\"time\":\"%s\"}",
                appointment.getId(), user.getFullName(),
                request.getDoctorName(), request.getAppointmentTime()));

        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled");
        }

        AppointmentStatus previousStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        logStatusChange(appointment, previousStatus, AppointmentStatus.CANCELLED, "Appointment cancelled");

        eventProducer.sendCancellationEvent(String.format(
                "{\"appointmentId\":%d,\"doctor\":\"%s\",\"time\":\"%s\"}",
                appointment.getId(), appointment.getDoctorName(), appointment.getAppointmentTime()));

        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getUserAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return appointmentRepository.findByUserId(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<LocalDateTime> getAvailableSlots(String doctorName, LocalDateTime date) {
        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Appointment> booked = appointmentRepository
                .findByDoctorAndTimeRange(doctorName, start, end);
        List<LocalDateTime> bookedTimes = booked.stream()
                .map(Appointment::getAppointmentTime).collect(Collectors.toList());

        List<LocalDateTime> allSlots = new java.util.ArrayList<>();
        LocalDateTime slot = start.plusHours(9);
        while (slot.isBefore(start.plusHours(17))) {
            if (!bookedTimes.contains(slot)) {
                allSlots.add(slot);
            }
            slot = slot.plusMinutes(30);
        }
        return allSlots;
    }

    private void logStatusChange(Appointment appointment, AppointmentStatus prev,
                                 AppointmentStatus next, String message) {
        AppointmentLog log = new AppointmentLog();
        log.setAppointment(appointment);
        log.setPreviousStatus(prev);
        log.setNewStatus(next);
        log.setMessage(message);
        appointmentLogRepository.save(log);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setDoctorName(appointment.getDoctorName());
        response.setSpecialization(appointment.getSpecialization());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setPatientName(appointment.getUser().getFullName());
        response.setCreatedAt(appointment.getCreatedAt());
        return response;
    }
}