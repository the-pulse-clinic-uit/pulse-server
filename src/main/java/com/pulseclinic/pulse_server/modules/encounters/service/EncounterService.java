package com.pulseclinic.pulse_server.modules.encounters.service;

import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;

public interface EncounterService {
    EncounterDto startEncounter(EncounterRequestDto encounterRequestDto);
    Optional<EncounterDto> getEncounterById(UUID encounterId);
    boolean recordDiagnosis(UUID encounterId, String diagnosis);
    boolean addNotes(UUID encounterId, String notes);
    boolean endEncounter(UUID encounterId);
    String generateSummary(UUID encounterId);
}
