package com.pulseclinic.pulse_server.modules.admissions.controller;

import java.util.UUID;

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

import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.admissions.service.AdmissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admissions")
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

    @PostMapping("/from_encounter/{encounterId}")
    public ResponseEntity<AdmissionDto> createFromEncounter(
            @PathVariable UUID encounterId,
            @Valid @RequestBody AdmissionRequestDto requestDto) {
        try {
            AdmissionDto admission = admissionService.createFromEncounter(encounterId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(admission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}")
    public ResponseEntity<AdmissionDto> getAdmissionById(@PathVariable UUID admissionId) {
        return admissionService.getAdmissionById(admissionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{admissionId}/transfer")
    public ResponseEntity<Void> transferRoom(
            @PathVariable UUID admissionId,
            @RequestParam UUID newRoomId) {
        boolean success = admissionService.transferRoom(admissionId, newRoomId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{admissionId}/discharge")
    public ResponseEntity<Void> dischargePatient(@PathVariable UUID admissionId) {
        boolean success = admissionService.dischargePatient(admissionId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{admissionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable UUID admissionId,
            @RequestParam String notes) {
        boolean success = admissionService.updateNotes(admissionId, notes);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
