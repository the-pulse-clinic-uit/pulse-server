package com.pulseclinic.pulse_server.modules.patients.dto;

import com.pulseclinic.pulse_server.enums.BloodType;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PatientRequestDto {
    @NotNull(message = "Health Insurance ID is required")
    private String healthInsuranceId;

    private BloodType bloodType;

    @Size(max = 100, message = "Please provide allergies if have any")
    private String allergies;

    // relationships
    @NotNull(message = "User ID is required")
    private UUID userId;
}
