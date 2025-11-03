package com.pulseclinic.pulse_server.modules.staff.dto.staff;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class StaffDto {
    private UUID id;

    private Position position;

    private LocalDateTime created_at;

    // relationships
    private UserDto user_dto;
}
