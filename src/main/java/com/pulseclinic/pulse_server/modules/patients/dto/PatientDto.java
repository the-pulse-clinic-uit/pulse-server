package com.pulseclinic.pulse_server.modules.patients.dto;

import com.pulseclinic.pulse_server.enums.BloodType;
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
public class PatientDto {
    private UUID id;

    private String health_insurance_id;

    private BloodType bloodType;

    private String allergies;

    private LocalDateTime created_at;

    // relationships)
    private UserDto user_dto;
}
