package com.pulseclinic.pulse_server.modules.ratings.dto;

import com.pulseclinic.pulse_server.enums.RatingType;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StaffRatingDto {
    private UUID id;

    private String comment;

    private String guest_contact_type;

    private String guest_contact_hash;

    private LocalDateTime created_at;

    private RatingType rater_type;

    private Integer rating; // 0<x<5

    // relationships => 3
    private StaffDto staff_dto;

    private PatientDto patient_dto; // optional

    private EncounterDto encounter_dto; // optional
}
