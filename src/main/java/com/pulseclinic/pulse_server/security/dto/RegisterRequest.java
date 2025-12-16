package com.pulseclinic.pulse_server.security.dto;

import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String full_name;

    @NotBlank
    private String password;

    @NotBlank
    private String citizen_id;

    @NotBlank
    private String phone;

    @NotNull
    private Boolean gender;

    @NotNull
    private LocalDate birth_date;

    private String address;        // optional
    private String avatar_url;     // optional

    @NotNull
    @Valid
    private RoleDto roleDto;
}
