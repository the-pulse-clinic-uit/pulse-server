package com.pulseclinic.pulse_server.modules.users.dto.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoleRequestDto {
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    private String name;
}
