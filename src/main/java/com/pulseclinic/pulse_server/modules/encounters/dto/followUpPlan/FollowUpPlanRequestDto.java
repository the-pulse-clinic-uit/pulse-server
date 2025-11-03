package com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan;

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FollowUpPlanRequestDto {
    @NotNull(message = "First Due At must be provided")
    private LocalDateTime first_due_at;

    private String rrule;

    private FollowUpPlanStatus status;

    private String notes;

    // relationships
    @NotNull(message = "Patient ID is required")
    private UUID patient_id;

    @NotNull(message = "Doctor ID is required")
    private UUID doctor_id;

    @NotNull(message = "Base Encounter ID is required")
    private UUID base_encounter_id;
}
