package com.pulseclinic.pulse_server.modules.encounters.dto.encounter;

import com.pulseclinic.pulse_server.enums.EncounterType;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
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
public class EncounterRequestDto {
    private EncounterType type;

    @NotNull(message = "Encounter started_at must be provided")
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @NotNull(message = "Diagnosis must be provided")
    private String diagnosis;

    private String notes;

    // relationships => 3
    private UUID appointmentId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
}
