package com.pulseclinic.pulse_server.modules.admissions.service;

import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;

public interface AdmissionService {
    AdmissionDto admitPatient(AdmissionRequestDto admissionRequestDto);
    Optional<AdmissionDto> getAdmissionById(UUID admissionId);
    boolean transferRoom(UUID admissionId, UUID newRoomId);
    boolean dischargePatient(UUID admissionId);
    boolean updateNotes(UUID admissionId, String notes);
}
