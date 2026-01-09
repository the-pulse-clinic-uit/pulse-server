package com.pulseclinic.pulse_server.modules.patients.service;

import com.pulseclinic.pulse_server.modules.patients.dto.PatientViolationSummary;

import java.util.UUID;

public interface PatientViolationService {
    PatientViolationSummary getViolationSummary(UUID patientId);
}
