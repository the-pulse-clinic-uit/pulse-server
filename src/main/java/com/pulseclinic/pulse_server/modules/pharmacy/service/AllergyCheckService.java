package com.pulseclinic.pulse_server.modules.pharmacy.service;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyWarning;

import java.util.List;
import java.util.UUID;

public interface AllergyCheckService {
    List<AllergyWarning> checkPatientAllergies(UUID patientId, List<UUID> drugIds);
}
