package com.pulseclinic.pulse_server.modules.encounters.controller;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.service.EncounterService;

import jakarta.validation.Valid;
@Slf4j
@RestController
@RequestMapping("/encounters")
public class EncounterController {
    private final EncounterService encounterService;
    
    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    // Start new encounter
    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<EncounterDto> startEncounter(@Valid @RequestBody EncounterRequestDto requestDto) {
        try {
            EncounterDto encounter = encounterService.startEncounter(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(encounter);
        } catch (Exception e) {
            log.info("Error starting encounter controller", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Get encounter by ID
    @GetMapping("/{encounterId}")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<EncounterDto> getEncounterById(@PathVariable UUID encounterId) {
        Optional<EncounterDto> encounter = encounterService.getEncounterById(encounterId);
        return encounter.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Record diagnosis
    @PutMapping("/{encounterId}/diagnosis")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> recordDiagnosis(
            @PathVariable UUID encounterId,
            @RequestParam String diagnosis) {
        boolean success = encounterService.recordDiagnosis(encounterId, diagnosis);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Add notes
    @PutMapping("/{encounterId}/notes")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> addNotes(
            @PathVariable UUID encounterId,
            @RequestParam String notes) {
        boolean success = encounterService.addNotes(encounterId, notes);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // End encounter
    @PostMapping("/{encounterId}/end")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> endEncounter(@PathVariable UUID encounterId) {
        boolean success = encounterService.endEncounter(encounterId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Generate summary
    @GetMapping("/{encounterId}/summary")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<String> generateSummary(@PathVariable UUID encounterId) {
        String summary = encounterService.generateSummary(encounterId);
        return ResponseEntity.ok(summary);
    }
}
