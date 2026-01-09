package com.pulseclinic.pulse_server.modules.patients.dto;

import com.pulseclinic.pulse_server.enums.BloodType;
import com.pulseclinic.pulse_server.enums.ViolationLevel;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PatientDto {
    private UUID id;

    private String healthInsuranceId;

    private BloodType bloodType;

    private String allergies;

    private LocalDateTime createdAt;

    private Boolean hasViolations;

    private ViolationLevel violationLevel;

    private Integer noShowCount;

    private BigDecimal outstandingDebt;

    private UserDto userDto;
}
