package com.pulseclinic.pulse_server.modules.appointments.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;
import com.pulseclinic.pulse_server.modules.appointments.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Schedule new appointment
    @PostMapping
    public ResponseEntity<AppointmentDto> scheduleAppointment(
            @Valid @RequestBody AppointmentRequestDto appointmentRequestDto) {
        try {
            AppointmentDto appointment = appointmentService.scheduleAppointment(appointmentRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get appointment by ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable UUID appointmentId) {
        Optional<AppointmentDto> appointment = appointmentService.getAppointmentById(appointmentId);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Reschedule appointment
    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<Void> rescheduleAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {
        boolean rescheduled = appointmentService.rescheduleAppointment(appointmentId, newStartTime, newEndTime);
        return rescheduled ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Cancel appointment
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String reason) {
        boolean cancelled = appointmentService.cancelAppointment(appointmentId, reason);
        return cancelled ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Confirm appointment
    @PutMapping("/{appointmentId}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable UUID appointmentId) {
        boolean confirmed = appointmentService.confirmAppointment(appointmentId);
        return confirmed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Check-in appointment
    @PutMapping("/{appointmentId}/checkin")
    public ResponseEntity<Void> checkIn(@PathVariable UUID appointmentId) {
        boolean checkedIn = appointmentService.checkIn(appointmentId);
        return checkedIn ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Mark appointment as done
    @PutMapping("/{appointmentId}/done")
    public ResponseEntity<Void> markAsDone(@PathVariable UUID appointmentId) {
        boolean marked = appointmentService.markAsDone(appointmentId);
        return marked ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Create encounter from appointment
    @PostMapping("/{appointmentId}/encounter")
    public ResponseEntity<Object> createEncounter(@PathVariable UUID appointmentId) {
        try {
            Object encounter = appointmentService.createEncounter(appointmentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(encounter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
