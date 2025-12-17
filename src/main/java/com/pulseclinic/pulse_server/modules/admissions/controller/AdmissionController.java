package com.pulseclinic.pulse_server.modules.admissions.controller;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

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

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.admissions.service.AdmissionService;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {
    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping
    public ResponseEntity<AdmissionDto> admitPatient(@Valid @RequestBody AdmissionRequestDto requestDto) {
        try {
            AdmissionDto admission = admissionService.admitPatient(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(admission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{admissionId}/transfer")
    public ResponseEntity<Void> transferRoom(
            @PathVariable UUID admissionId,
            @RequestParam UUID newRoomId) {
        try {
            boolean success = admissionService.transferRoom(admissionId, newRoomId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{admissionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable UUID admissionId,
            @RequestParam String notes) {
        try {
            boolean success = admissionService.updateNotes(admissionId, notes);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{admissionId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID admissionId,
            @RequestParam AdmissionStatus status) {
        try {
            boolean success = admissionService.updateStatus(admissionId, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{admissionId}/discharge")
    public ResponseEntity<Void> dischargePatient(@PathVariable UUID admissionId) {
        try {
            boolean success = admissionService.dischargePatient(admissionId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}/duration")
    public ResponseEntity<Duration> getDuration(@PathVariable UUID admissionId) {
        try {
            Duration duration = admissionService.getDuration(admissionId);
            return ResponseEntity.ok(duration);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{admissionId}/patient")
    public ResponseEntity<PatientDto> getPatient(@PathVariable UUID admissionId) {
        try {
            Optional<PatientDto> patient = admissionService.getPatient(admissionId);
            return patient.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}/doctor")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable UUID admissionId) {
        try {
            Optional<DoctorDto> doctor = admissionService.getDoctor(admissionId);
            return doctor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}/room")
    public ResponseEntity<RoomDto> getRoom(@PathVariable UUID admissionId) {
        try {
            Optional<RoomDto> room = admissionService.getRoom(admissionId);
            return room.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}/is-ongoing")
    public ResponseEntity<Boolean> isOngoing(@PathVariable UUID admissionId) {
        try {
            boolean ongoing = admissionService.isOngoing(admissionId);
            return ResponseEntity.ok(ongoing);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{admissionId}/can-transfer")
    public ResponseEntity<Boolean> canTransfer(@PathVariable UUID admissionId) {
        try {
            boolean canTransfer = admissionService.canTransfer(admissionId);
            return ResponseEntity.ok(canTransfer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{admissionId}/can-discharge")
    public ResponseEntity<Boolean> canDischarge(@PathVariable UUID admissionId) {
        try {
            boolean canDischarge = admissionService.canDischarge(admissionId);
            return ResponseEntity.ok(canDischarge);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{admissionId}")
    public ResponseEntity<Void> deleteAdmission(@PathVariable UUID admissionId) {
        try {
            boolean success = admissionService.deleteAdmission(admissionId);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
