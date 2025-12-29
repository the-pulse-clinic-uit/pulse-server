package com.pulseclinic.pulse_server.modules.staff.dto.staff;

import com.pulseclinic.pulse_server.enums.Position;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class StaffRequestDto {
    @NotNull(message = "Position is required")
    private Position position;

    // relationships
    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID departmentId;
}
