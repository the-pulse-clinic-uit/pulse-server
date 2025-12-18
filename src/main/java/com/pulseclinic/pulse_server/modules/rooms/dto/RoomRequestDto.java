package com.pulseclinic.pulse_server.modules.rooms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RoomRequestDto {
    @NotBlank(message = "Room Number must be provided")
    private String roomNumber; // etc B104

    @NotNull(message = "Bed amount is required")
    private Integer bedAmount;

    private Boolean isAvailable = true;

    // relationships
    private UUID departmentId;
}
