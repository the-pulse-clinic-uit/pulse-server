package com.pulseclinic.pulse_server.modules.patients.service;

import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientSearchDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientService {
    Patient registerPatient(PatientRequestDto patientRequestDto);
    Optional<Patient> findById(UUID id);
    Optional<Patient> findByEmail(String email);

    List<Patient> search(PatientSearchDto patientSearchDto);
    List<Patient> getPatients();

    Patient updatePatient(UUID id, PatientDto patientDto);
    Patient updatePatientMe(String email, PatientDto patientDto);
}
