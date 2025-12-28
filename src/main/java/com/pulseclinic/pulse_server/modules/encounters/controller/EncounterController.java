package com.pulseclinic.pulse_server.modules.encounters.controller;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.service.EncounterService;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/encounters")
public class EncounterController {
    private final EncounterService encounterService;
    private final PatientRepository patientRepository;

    public EncounterController(EncounterService encounterService, PatientRepository patientRepository) {
        this.encounterService = encounterService;
        this.patientRepository = patientRepository;
    }

    // Start new encounter
    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<EncounterDto> startEncounter(@Valid @RequestBody EncounterRequestDto requestDto) {
        try {
            EncounterDto encounter = encounterService.startEncounter(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(encounter);
        } catch (Exception e) {
            log.info("Error starting encounter controller", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // get encounter by ID
    @GetMapping("/{encounterId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<EncounterDto> getEncounterById(@PathVariable UUID encounterId) {
        Optional<EncounterDto> encounter = encounterService.getEncounterById(encounterId);
        return encounter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // get all encounters
    @GetMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getAllEncounters() {
        List<EncounterDto> encounters = encounterService.getAllEncounters();
        return ResponseEntity.ok(encounters);
    }

    // get by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EncounterDto>> getEncountersByPatient(@PathVariable UUID patientId) {
        List<EncounterDto> encounters = encounterService.getEncountersByPatient(patientId);
        return ResponseEntity.ok(encounters);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('patient')")
    public ResponseEntity<List<EncounterDto>> getMyEncounters(Authentication authentication) {
        String email = authentication.getName();
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<EncounterDto> encounters = encounterService.getEncountersByPatient(patient.get().getId());
        return ResponseEntity.ok(encounters);
    }

    // get by doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getEncountersByDoctor(@PathVariable UUID doctorId) {
        List<EncounterDto> encounters = encounterService.getEncountersByDoctor(doctorId);
        return ResponseEntity.ok(encounters);
    }

    // get by patient and doctor
    @GetMapping("/patient/{patientId}/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getEncountersByPatientAndDoctor(
            @PathVariable UUID patientId,
            @PathVariable UUID doctorId) {
        List<EncounterDto> encounters = encounterService.getEncountersByPatientAndDoctor(patientId, doctorId);
        return ResponseEntity.ok(encounters);
    }

    // get by type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getEncountersByType(
            @PathVariable com.pulseclinic.pulse_server.enums.EncounterType type) {
        List<EncounterDto> encounters = encounterService.getEncountersByType(type);
        return ResponseEntity.ok(encounters);
    }

    // get active (not ended)
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getActiveEncounters() {
        List<EncounterDto> encounters = encounterService.getActiveEncounters();
        return ResponseEntity.ok(encounters);
    }

    // get completed (ended)
    @GetMapping("/completed")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getCompletedEncounters() {
        List<EncounterDto> encounters = encounterService.getCompletedEncounters();
        return ResponseEntity.ok(encounters);
    }

    // get today's encounters
    @GetMapping("/today")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getTodayEncounters() {
        List<EncounterDto> encounters = encounterService.getTodayEncounters();
        return ResponseEntity.ok(encounters);
    }

    // get by date range
    @GetMapping("/range")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<EncounterDto>> getEncountersByDateRange(
            @RequestParam java.time.LocalDateTime startDate,
            @RequestParam java.time.LocalDateTime endDate) {
        List<EncounterDto> encounters = encounterService.getEncountersByDateRange(startDate, endDate);
        return ResponseEntity.ok(encounters);
    }

    // get by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<EncounterDto> getEncounterByAppointment(@PathVariable UUID appointmentId) {
        Optional<EncounterDto> encounter = encounterService.getEncounterByAppointment(appointmentId);
        return encounter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Record diagnosis
    @PutMapping("/{encounterId}/diagnosis")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> recordDiagnosis(
            @PathVariable UUID encounterId,
            @RequestParam String diagnosis) {
        boolean success = encounterService.recordDiagnosis(encounterId, diagnosis);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Add notes
    @PutMapping("/{encounterId}/notes")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> addNotes(
            @PathVariable UUID encounterId,
            @RequestParam String notes) {
        boolean success = encounterService.addNotes(encounterId, notes);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // End encounter
    @PostMapping("/{encounterId}/end")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> endEncounter(@PathVariable UUID encounterId) {
        boolean success = encounterService.endEncounter(encounterId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Generate summary
    @GetMapping("/{encounterId}/summary")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<String> generateSummary(@PathVariable UUID encounterId) {
        String summary = encounterService.generateSummary(encounterId);
        return ResponseEntity.ok(summary);
    }
}
