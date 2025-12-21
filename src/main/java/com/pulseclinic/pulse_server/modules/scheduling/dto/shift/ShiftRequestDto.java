package com.pulseclinic.pulse_server.modules.scheduling.dto.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Start Time is required")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Start Time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Slot minutes is required")
    private Integer slotMinutes;

    private Integer capacityPerSlot; // default 1

    // relationship
    private UUID departmentId;

    private UUID defaultRoomId;
}
