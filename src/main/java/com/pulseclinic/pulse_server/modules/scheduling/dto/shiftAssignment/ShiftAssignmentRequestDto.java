package com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShiftAssignmentRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Duty Date is required")
    private LocalDate dutyDate;

    @NotNull(message = "Role in Shift is required. Valid values: 'ON_CALL', 'PRIMARY'")
    private ShiftAssignmentRole roleInShift;

    @NotNull(message = "Shift Assignment Status is required. Valid values: 'ACTIVE', 'CANCELLED'")
    private ShiftAssignmentStatus status;

    private String notes;

    // relationships 3
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Shift ID is required")
    private UUID shiftId;

    private UUID roomId; // can override
}
