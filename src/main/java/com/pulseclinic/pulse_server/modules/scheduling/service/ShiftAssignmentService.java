package com.pulseclinic.pulse_server.modules.scheduling.service;

import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftAssignmentService {
    ShiftAssignmentDto assignDoctor(ShiftAssignmentRequestDto shiftAssignmentRequestDto);
    boolean updateStatus(UUID assignmentId, ShiftAssignmentStatus status);
    boolean updateRoom(UUID assignmentId, UUID roomId);
    List<ShiftAssignmentDto> findByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate);
    List<ShiftAssignmentDto> findByShift(UUID shiftId, LocalDate date);
    Optional<ShiftAssignmentDto> getAssignmentById(UUID assignmentId);
    List<ShiftAssignmentDto> getAllAssignments();
}

