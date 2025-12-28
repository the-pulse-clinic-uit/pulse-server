package com.pulseclinic.pulse_server.modules.encounters.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.enums.EncounterType;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;

public interface EncounterService {
    EncounterDto startEncounter(EncounterRequestDto encounterRequestDto);
    boolean recordDiagnosis(UUID encounterId, String diagnosis);
    boolean addNotes(UUID encounterId, String notes);
    boolean endEncounter(UUID encounterId);
    String generateSummary(UUID encounterId);

    Optional<EncounterDto> getEncounterById(UUID encounterId);
    List<EncounterDto> getAllEncounters();
    List<EncounterDto> getEncountersByPatient(UUID patientId);
    List<EncounterDto> getEncountersByDoctor(UUID doctorId);
    List<EncounterDto> getEncountersByPatientAndDoctor(UUID patientId, UUID doctorId);
    List<EncounterDto> getEncountersByType(EncounterType type);
    List<EncounterDto> getActiveEncounters();
    List<EncounterDto> getCompletedEncounters();
    List<EncounterDto> getEncountersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<EncounterDto> getTodayEncounters();
    Optional<EncounterDto> getEncounterByAppointment(UUID appointmentId);
}
