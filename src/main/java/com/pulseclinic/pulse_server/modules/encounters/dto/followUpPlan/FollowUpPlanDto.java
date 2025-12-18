package com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan;

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FollowUpPlanDto {
    private UUID id;

    private LocalDateTime firstDueAt;

    private String rrule;

    private FollowUpPlanStatus status;

    private String notes;

    private LocalDateTime createdAt;
    // relationships
    private PatientDto patientDto;

    private DoctorDto doctorDto;

    private EncounterDto baseEncounterDto;
}
