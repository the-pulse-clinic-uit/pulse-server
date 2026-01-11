package com.pulseclinic.pulse_server.modules.appointments.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;

    public AppointmentController(AppointmentService appointmentService, PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.patientRepository = patientRepository;
    }

    // Schedule new appointment
    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff', 'patient')")
    public ResponseEntity<AppointmentDto> scheduleAppointment(
            @Valid @RequestBody AppointmentRequestDto appointmentRequestDto) {
        try {
            AppointmentDto appointment = appointmentService.scheduleAppointment(appointmentRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            log.error("Error message: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
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
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
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
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> confirmAppointment(@PathVariable UUID appointmentId) {
        boolean confirmed = appointmentService.confirmAppointment(appointmentId);
        return confirmed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{appointmentId}/noshow")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> noshowAppointment(@PathVariable UUID appointmentId) {
        boolean noshowed = appointmentService.noshowAppointment(appointmentId);
        return noshowed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Check-in appointment
    @PutMapping("/{appointmentId}/checkin")
    public ResponseEntity<Void> checkIn(@PathVariable UUID appointmentId) {
        boolean checkedIn = appointmentService.checkIn(appointmentId);
        return checkedIn ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Mark appointment as done
    @PutMapping("/{appointmentId}/done")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> markAsDone(@PathVariable UUID appointmentId) {
        boolean marked = appointmentService.markAsDone(appointmentId);
        return marked ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Create encounter from appointment
    @PostMapping("/{appointmentId}/encounter")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Object> createEncounter(@PathVariable UUID appointmentId) {
        try {
            Object encounter = appointmentService.createEncounter(appointmentId);
            if (encounter == null) {
                log.error("Failed to create encounter for appointment {}: appointment not found or invalid status", appointmentId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(encounter);
        } catch (Exception e) {
            log.error("Error creating encounter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get all appointments
    @GetMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getAllAppointments() {
        java.util.List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('patient')")
    public ResponseEntity<java.util.List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        java.util.List<AppointmentDto> appointments = appointmentService.getAppointmentsByPatient(patient.get().getId());
        return ResponseEntity.ok(appointments);
    }

    // Get all pending appointments (for staff approval)
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getPendingAppointments() {
        java.util.List<AppointmentDto> appointments = appointmentService.getPendingAppointments();
        return ResponseEntity.ok(appointments);
    }

    // Get all confirmed appointments
    @GetMapping("/confirmed")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getConfirmedAppointments() {
        java.util.List<AppointmentDto> appointments = appointmentService.getConfirmedAppointments();
        return ResponseEntity.ok(appointments);
    }

    // Get today's appointments
    @GetMapping("/today")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getTodayAppointments() {
        java.util.List<AppointmentDto> appointments = appointmentService.getTodayAppointments();
        return ResponseEntity.ok(appointments);
    }

    // Get appointments by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getAppointmentsByStatus(
            @PathVariable com.pulseclinic.pulse_server.enums.AppointmentStatus status) {
        java.util.List<AppointmentDto> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    // Get appointments by doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getAppointmentsByDoctor(@PathVariable UUID doctorId) {
        java.util.List<AppointmentDto> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    // Get appointments by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<java.util.List<AppointmentDto>> getAppointmentsByPatient(@PathVariable UUID patientId) {
        java.util.List<AppointmentDto> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    // Get appointments by date range
    @GetMapping("/range")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<java.util.List<AppointmentDto>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        java.util.List<AppointmentDto> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }
}
