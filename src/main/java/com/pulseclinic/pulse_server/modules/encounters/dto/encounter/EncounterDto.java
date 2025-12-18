package com.pulseclinic.pulse_server.modules.encounters.dto.encounter;

import com.pulseclinic.pulse_server.enums.EncounterType;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
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
public class EncounterDto {
    private UUID id;

    private EncounterType type;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private String diagnosis;

    private String notes;

    private LocalDateTime createdAt;

    // relationships => 3
    private AppointmentDto appointmentDto;

    private PatientDto patientDto;

    private DoctorDto doctorDto;
}
