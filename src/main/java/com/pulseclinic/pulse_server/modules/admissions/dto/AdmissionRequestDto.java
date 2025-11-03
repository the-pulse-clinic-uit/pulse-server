package com.pulseclinic.pulse_server.modules.admissions.dto;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmissionRequestDto {
    private AdmissionStatus status;

    private String notes;

    // relationships => 4
    private EncounterDto encounter_dto; // optional

    @NotNull(message = "Patient ID is required")
    private PatientDto patient_dto;

    @NotNull(message = "Doctor ID is required")
    private DoctorDto doctor_dto;

    @NotNull(message = "Room ID is required")
    private RoomDto room_dto;
}
