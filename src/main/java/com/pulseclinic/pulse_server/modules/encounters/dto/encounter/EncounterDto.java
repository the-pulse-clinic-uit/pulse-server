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

    private LocalDateTime started_at;

    private LocalDateTime ended_at;

    private String diagnosis;

    private String notes;

    private LocalDateTime created_at;

    // relationships => 3
    private AppointmentDto appointment_dto;

    private PatientDto patient_dto;

    private DoctorDto doctor_dto;
}
