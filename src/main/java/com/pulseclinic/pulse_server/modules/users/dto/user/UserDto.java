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

    private String hashed_password;

    private String full_name;

    private String address;

    private String citizen_id;

    private String phone;

    private Boolean gender;

    private LocalDate birth_date;

    private String avatar_url;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private Boolean is_active;

    // relationships
    private RoleDto role_dto;
}
