package com.pulseclinic.pulse_server.modules.admissions.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pulseclinic.pulse_server.mappers.impl.AdmissionMapper;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Slf4j
@RestController
@RequestMapping("/admissions")
public class AdmissionController {
    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<AdmissionDto>> getAllAdmissions(){
        List<AdmissionDto> allAdmissions = this.admissionService.getAllAdmissions();
        return new ResponseEntity<>(allAdmissions ,HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor','staff')")
    public ResponseEntity<AdmissionDto> admitPatient(@Valid @RequestBody AdmissionRequestDto requestDto) {
        try {
            AdmissionDto admission = admissionService.admitPatient(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(admission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{admissionId}")
    @PreAuthorize("hasAnyAuthority('doctor','staff')")
    public ResponseEntity<AdmissionDto> getAdmissionById(@PathVariable UUID admissionId) {
        return admissionService.getAdmissionById(admissionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{admissionId}/transfer-room")
    @PreAuthorize("hasAnyAuthority('doctor','staff')")
    public ResponseEntity<Void> transferRoom(
            @PathVariable UUID admissionId,
            @RequestParam UUID newRoomId) {
        try {
            boolean success = admissionService.transferRoom(admissionId, newRoomId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to transfer room: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{admissionId}/discharge")
    @PreAuthorize("hasAnyAuthority('doctor','staff')")
    public ResponseEntity<Void> dischargePatient(@PathVariable UUID admissionId) {
        try {
            boolean success = admissionService.dischargePatient(admissionId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // log.info("Exception caught: {}", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{admissionId}/notes")
    @PreAuthorize("hasAnyAuthority('doctor','staff')")
    public ResponseEntity<Void> updateNotes(
            @PathVariable UUID admissionId,
            @RequestParam String notes) {
        boolean success = admissionService.updateNotes(admissionId, notes);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
