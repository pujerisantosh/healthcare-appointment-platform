package com.santosh.healthcarebackend.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendBookingEvent(String message) {
        log.info("Publishing booking event: {}", message);
        kafkaTemplate.send("appointment-booked", message);
    }

    public void sendCancellationEvent(String message) {
        log.info("Publishing cancellation event: {}", message);
        kafkaTemplate.send("appointment-cancelled", message);
    }
}