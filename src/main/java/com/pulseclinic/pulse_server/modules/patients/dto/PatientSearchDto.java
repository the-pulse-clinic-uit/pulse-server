package com.pulseclinic.pulse_server.modules.patients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientSearchDto {
    UUID patient_id;
    String citizen_id;
    String fullName;
    String email;
}
