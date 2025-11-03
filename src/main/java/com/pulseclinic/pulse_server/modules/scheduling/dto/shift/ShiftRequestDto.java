package com.pulseclinic.pulse_server.modules.scheduling.dto.shift;

import com.pulseclinic.pulse_server.enums.ShiftKind;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShiftRequestDto {
    @NotNull(message = "Name the Shift!")
    private String name;

    @NotNull(message = "Not Null. Valid values: 'ER', 'CLINIC'")
    private ShiftKind kind;

    @NotNull(message = "Start Time is required")
    private LocalDateTime start_time;

    @NotNull(message = "Start Time is required")
    private LocalDateTime end_time;

    @NotNull(message = "Slot minutes is required")
    private Integer slot_minutes;

    private Integer capacity_per_slot; // default 1

    // relationship
    private UUID department_id;

    private UUID default_room_id;
}
