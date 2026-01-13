package com.pulseclinic.pulse_server.modules.encounters.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
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

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.service.FollowUpPlanService;

import jakarta.validation.Valid;

@Slf4j
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
            if (requestDto.getBaseEncounterId() == null) {
                return ResponseEntity.badRequest().build();
            }
            FollowUpPlanDto plan = followUpPlanService.createPlan(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(plan);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/from_encounter/{encounterId}")
    public ResponseEntity<FollowUpPlanDto> createFromEncounter(
            @PathVariable UUID encounterId,
            @Valid @RequestBody FollowUpPlanRequestDto requestDto) {
        FollowUpPlanDto plan = followUpPlanService.createFromEncounter(encounterId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
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
        followUpPlanService.editPlan(planId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/pause")
    public ResponseEntity<Void> pausePlan(@PathVariable UUID planId) {
        followUpPlanService.pausePlan(planId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/resume")
    public ResponseEntity<Void> resumePlan(@PathVariable UUID planId) {
        followUpPlanService.resumePlan(planId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/complete")
    public ResponseEntity<Void> completePlan(@PathVariable UUID planId) {
        followUpPlanService.completePlan(planId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID planId) {
        followUpPlanService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{planId}/generate_appointments")
    public ResponseEntity<List<AppointmentDto>> generateAppointments(@PathVariable UUID planId) {
        List<AppointmentDto> appointments = followUpPlanService.generateAppointments(planId);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }
    
    // Query endpoints
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FollowUpPlanDto>> getByPatient(@PathVariable UUID patientId) {
        List<FollowUpPlanDto> plans = followUpPlanService.getByPatient(patientId);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<FollowUpPlanDto>> getByDoctor(@PathVariable UUID doctorId) {
        List<FollowUpPlanDto> plans = followUpPlanService.getByDoctor(doctorId);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<FollowUpPlanDto>> getByPatientAndStatus(
            @PathVariable UUID patientId,
            @PathVariable FollowUpPlanStatus status) {
        List<FollowUpPlanDto> plans = followUpPlanService.getByPatientAndStatus(patientId, status);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<FollowUpPlanDto>> getUpcomingPlans(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<FollowUpPlanDto> plans = followUpPlanService.getUpcomingPlans(startDate, endDate);
        return ResponseEntity.ok(plans);
    }
}
