package com.pulseclinic.pulse_server.modules.users.dto.role;

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
public class RoleDto {
    private UUID id;

    private String name;

    private LocalDateTime createdAt;
}
