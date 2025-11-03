package com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment;

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
    @NotNull(message = "Duty Date is required")
    private LocalDate duty_date;

    @NotNull(message = "Role in Shift is required. Valid values: 'ON_CALL', 'PRIMARY'")
    private ShiftAssignmentRole role_in_shift;

    @NotNull(message = "Shift Assignment Status is required. Valid values: 'ACTIVE', 'CANCELLED'")
    private ShiftAssignmentStatus status;

    private String notes;

    // relationships 3
    @NotNull(message = "Doctor ID is required")
    private UUID doctor_id;

    @NotNull(message = "Shift ID is required")
    private UUID shift_id;

    private UUID room_id; // can override
}
