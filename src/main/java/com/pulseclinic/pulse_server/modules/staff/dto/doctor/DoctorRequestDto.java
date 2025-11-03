package com.pulseclinic.pulse_server.modules.staff.dto.doctor;

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
public class DoctorRequestDto {
    @NotNull(message = "License ID is required")
    private String license_id;

    private Boolean is_verified;

    // relationships => 2

    @NotNull(message = "Staff ID must not be null")
    private UUID staff_id;

    @NotNull(message = "Department ID must not be null")
    private UUID department_id;
}
