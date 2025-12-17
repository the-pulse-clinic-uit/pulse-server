package com.pulseclinic.pulse_server.modules.appointments.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
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

    // Get all appointments
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.findAll();
        return ResponseEntity.ok(appointments);
    }

    // Get appointment by ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable UUID appointmentId) {
        Optional<AppointmentDto> appointment = appointmentService.getAppointmentById(appointmentId);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get appointments by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByPatient(@PathVariable UUID patientId) {
        List<AppointmentDto> appointments = appointmentService.findByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    // Get upcoming appointments by patient
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentDto>> getUpcomingAppointmentsByPatient(@PathVariable UUID patientId) {
        List<AppointmentDto> appointments = appointmentService.findUpcomingByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    // Update appointment status
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID appointmentId,
            @RequestParam AppointmentStatus status) {
        boolean updated = appointmentService.updateStatus(appointmentId, status);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Cancel appointment
    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String reason) {
        boolean cancelled = appointmentService.cancelAppointment(appointmentId, reason);
        return cancelled ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Reschedule appointment
    @PostMapping("/{appointmentId}/reschedule")
    public ResponseEntity<Void> rescheduleAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {
        boolean rescheduled = appointmentService.rescheduleAppointment(appointmentId, newStartTime, newEndTime);
        return rescheduled ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Confirm appointment
    @PostMapping("/{appointmentId}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable UUID appointmentId) {
        boolean confirmed = appointmentService.confirmAppointment(appointmentId);
        return confirmed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Check-in appointment
    @PostMapping("/{appointmentId}/check-in")
    public ResponseEntity<Void> checkIn(@PathVariable UUID appointmentId) {
        boolean checkedIn = appointmentService.checkIn(appointmentId);
        return checkedIn ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Mark appointment as done
    @PostMapping("/{appointmentId}/done")
    public ResponseEntity<Void> markAsDone(@PathVariable UUID appointmentId) {
        boolean marked = appointmentService.markAsDone(appointmentId);
        return marked ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Mark appointment as no-show
    @PostMapping("/{appointmentId}/no-show")
    public ResponseEntity<Void> markAsNoShow(@PathVariable UUID appointmentId) {
        boolean marked = appointmentService.markAsNoShow(appointmentId);
        return marked ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Check conflicts
    @GetMapping("/{appointmentId}/conflicts")
    public ResponseEntity<Boolean> checkConflicts(@PathVariable UUID appointmentId) {
        boolean hasConflicts = appointmentService.checkConflicts(appointmentId);
        return ResponseEntity.ok(hasConflicts);
    }

    // Validate time slot
    @GetMapping("/{appointmentId}/validate-time-slot")
    public ResponseEntity<Boolean> validateTimeSlot(@PathVariable UUID appointmentId) {
        boolean isValid = appointmentService.validateTimeSlot(appointmentId);
        return ResponseEntity.ok(isValid);
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

    // Send reminder
    @PostMapping("/{appointmentId}/reminder")
    public ResponseEntity<Void> sendReminder(@PathVariable UUID appointmentId) {
        boolean sent = appointmentService.sendReminder(appointmentId);
        return sent ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Check if can cancel
    @GetMapping("/{appointmentId}/can-cancel")
    public ResponseEntity<Boolean> canCancel(@PathVariable UUID appointmentId) {
        boolean canCancel = appointmentService.canCancel(appointmentId);
        return ResponseEntity.ok(canCancel);
    }

    // Check if can reschedule
    @GetMapping("/{appointmentId}/can-reschedule")
    public ResponseEntity<Boolean> canReschedule(@PathVariable UUID appointmentId) {
        boolean canReschedule = appointmentService.canReschedule(appointmentId);
        return ResponseEntity.ok(canReschedule);
    }

    // Delete appointment
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        boolean deleted = appointmentService.deleteAppointment(appointmentId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
