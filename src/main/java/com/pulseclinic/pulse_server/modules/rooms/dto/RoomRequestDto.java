package com.pulseclinic.pulse_server.modules.rooms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RoomRequestDto {
    @NotBlank(message = "Room Number must be provided")
    private String room_number; // etc B104

    @NotBlank(message = "Bed amount is required")
    private Integer bed_amount;

    private Boolean is_available = true;

    // relationships
    private UUID department_id;
}
