package com.pulseclinic.pulse_server.modules.encounters.dto.encounter;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @NotNull(message = "Encounter ended_at must be provided")
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
