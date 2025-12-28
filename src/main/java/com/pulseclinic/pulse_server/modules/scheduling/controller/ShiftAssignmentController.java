package com.pulseclinic.pulse_server.modules.scheduling.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftAssignmentService;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/shifts")
public class ShiftAssignmentController {
    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
    }

    // Assign doctor to shift
    @PostMapping("/assignments")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<ShiftAssignmentDto> assignDoctor(@Valid @RequestBody ShiftAssignmentRequestDto requestDto) {
        try {
            ShiftAssignmentDto assignment = shiftAssignmentService.assignDoctor(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
        } catch (Exception e) {
            log.error("Error assigning doctor: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Get assignment by ID
    @GetMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<ShiftAssignmentDto> getAssignmentById(@PathVariable UUID assignmentId) {
        Optional<ShiftAssignmentDto> assignment = shiftAssignmentService.getAssignmentById(assignmentId);
        return assignment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update assignment status
    @PutMapping("/assignments/{assignmentId}/status")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID assignmentId,
            @RequestParam ShiftAssignmentStatus status) {
        boolean success = shiftAssignmentService.updateStatus(assignmentId, status);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Update assignment room
    @PutMapping("/assignments/{assignmentId}/room")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> updateRoom(
            @PathVariable UUID assignmentId,
            @RequestParam UUID roomId) {
        boolean success = shiftAssignmentService.updateRoom(assignmentId, roomId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Get all assignments for a shift on a specific date
    @GetMapping("/{shiftId}/assignments")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<ShiftAssignmentDto>> findByShift(
            @PathVariable UUID shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShiftAssignmentDto> assignments = shiftAssignmentService.findByShift(shiftId, date);
        return ResponseEntity.ok(assignments);
    }

    // Get assignments by doctor within date range
    @GetMapping("/assignments/by_doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<ShiftAssignmentDto>> findByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ShiftAssignmentDto> assignments = shiftAssignmentService.findByDoctor(doctorId, startDate, endDate);
        return ResponseEntity.ok(assignments);
    }
}
