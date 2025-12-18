package com.pulseclinic.pulse_server.modules.staff.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorRequestDto doctorRequestDto) {
        try {
            DoctorDto doctor = doctorService.createDoctor(doctorRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    // Get doctor by ID
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable UUID doctorId) {
        Optional<DoctorDto> doctor = doctorService.getDoctorById(doctorId);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update doctor basic info
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable UUID doctorId,
            @Valid @RequestBody DoctorRequestDto doctorRequestDto) {
        try {
            DoctorDto updatedDoctor = doctorService.updateDoctor(doctorId, doctorRequestDto);
            return ResponseEntity.ok(updatedDoctor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Update doctor specialization
    @PutMapping("/{doctorId}/specialization")
    public ResponseEntity<Void> updateSpecialization(
            @PathVariable UUID doctorId,
            @RequestParam UUID departmentId) {
        boolean updated = doctorService.updateSpecialization(doctorId, departmentId);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Get doctor's patients
    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<List<Object>> getPatients(@PathVariable UUID doctorId) {
        List<Object> patients = doctorService.getPatients(doctorId);
        return ResponseEntity.ok(patients);
    }

    // Get doctor's shift schedule
    @GetMapping("/{doctorId}/schedule")
    public ResponseEntity<List<Object>> getShiftSchedule(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Object> shifts = doctorService.getShiftSchedule(doctorId, date);
        return ResponseEntity.ok(shifts);
    }

    // Check doctor availability
    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isAvailable = doctorService.checkAvailability(doctorId, dateTime);
        return ResponseEntity.ok(isAvailable);
    }
}
