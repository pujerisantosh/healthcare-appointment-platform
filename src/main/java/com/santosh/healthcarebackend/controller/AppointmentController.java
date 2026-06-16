package com.santosh.healthcarebackend.controller;

import com.santosh.healthcarebackend.dto.AppointmentRequest;
import com.santosh.healthcarebackend.dto.AppointmentResponse;
import com.santosh.healthcarebackend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getUserAppointments());
    }

    @GetMapping("/slots")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @RequestParam String doctorName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorName, date));
    }
}