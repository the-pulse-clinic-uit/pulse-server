package com.pulseclinic.pulse_server.modules.encounters.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.service.FollowUpPlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/followup/plans")
public class FollowUpPlanController {
    private final FollowUpPlanService followUpPlanService;
    
    public FollowUpPlanController(FollowUpPlanService followUpPlanService) {
        this.followUpPlanService = followUpPlanService;
    }

    @PostMapping
    public ResponseEntity<FollowUpPlanDto> createPlan(@Valid @RequestBody FollowUpPlanRequestDto requestDto) {
        try {
            FollowUpPlanDto plan = followUpPlanService.createPlan(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(plan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/from_encounter/{encounterId}")
    public ResponseEntity<FollowUpPlanDto> createFromEncounter(
            @PathVariable UUID encounterId,
            @Valid @RequestBody FollowUpPlanRequestDto requestDto) {
        try {
            FollowUpPlanDto plan = followUpPlanService.createFromEncounter(encounterId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(plan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{planId}")
    public ResponseEntity<FollowUpPlanDto> getFollowUpPlanById(@PathVariable UUID planId) {
        return followUpPlanService.getFollowUpPlanById(planId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{planId}")
    public ResponseEntity<Void> editPlan(
            @PathVariable UUID planId,
            @Valid @RequestBody FollowUpPlanRequestDto requestDto) {
        boolean success = followUpPlanService.editPlan(planId, requestDto);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{planId}/pause")
    public ResponseEntity<Void> pausePlan(@PathVariable UUID planId) {
        boolean success = followUpPlanService.pausePlan(planId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{planId}/resume")
    public ResponseEntity<Void> resumePlan(@PathVariable UUID planId) {
        boolean success = followUpPlanService.resumePlan(planId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{planId}/complete")
    public ResponseEntity<Void> completePlan(@PathVariable UUID planId) {
        boolean success = followUpPlanService.completePlan(planId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{planId}/generate_appointments")
    public ResponseEntity<List<AppointmentDto>> generateAppointments(@PathVariable UUID planId) {
        try {
            List<AppointmentDto> appointments = followUpPlanService.generateAppointments(planId);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
