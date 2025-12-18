package com.pulseclinic.pulse_server.modules.users.dto.user;

import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
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
public class UserDto {
    private UUID id;

    private String email;

    private String hashedPassword;

    private String fullName;

    private String address;

    private String citizenId;

    private String phone;

    private Boolean gender;

    private LocalDate birthDate;

    private String avatarUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    // relationships
    private RoleDto roleDto;
}
