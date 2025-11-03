package com.pulseclinic.pulse_server.modules.ratings.dto;

import com.pulseclinic.pulse_server.enums.RatingType;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class StaffRatingRequestDto {
    private String comment;

    private String guest_contact_type;

    private String guest_contact_hash;

    private RatingType rater_type;

    @NotNull(message = "Rating must be provided")
    @PositiveOrZero(message = "Rating should be >0")
    @Max(value = 5)
    private Integer rating; // 0<x<5

    // relationships => 3
    @NotNull(message = "Staff ID is required")
    private UUID staff_id;

    private UUID patient_id; // optional

    private UUID encounter_id; // optional
}
