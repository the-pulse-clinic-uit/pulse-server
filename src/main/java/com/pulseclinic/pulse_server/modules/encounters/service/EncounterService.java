package com.pulseclinic.pulse_server.modules.encounters.service;

import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EncounterService {
    EncounterDto startEncounter(EncounterRequestDto encounterRequestDto);
    boolean recordDiagnosis(UUID encounterId, String diagnosis);
    boolean addNotes(UUID encounterId, String notes);
    boolean endEncounter(UUID encounterId);
    Object createInvoice(UUID encounterId); // TODO: Return InvoiceDto when invoice module is implemented
    Object createPrescription(UUID encounterId); // TODO: Return PrescriptionDto when pharmacy module is implemented
    Object admitPatient(UUID encounterId, UUID roomId); // TODO: Return AdmissionDto when admission module is implemented
    FollowUpPlanDto createFollowUpPlan(UUID encounterId, String rrule, String notes);
    Duration getDuration(UUID encounterId);
    boolean isComplete(UUID encounterId);
    List<Object> getPrescriptions(UUID encounterId); // TODO: Return List<PrescriptionDto>
    List<Object> getInvoices(UUID encounterId); // TODO: Return List<InvoiceDto>
    Optional<Object> getAdmission(UUID encounterId); // TODO: Return Optional<AdmissionDto>
    String generateSummary(UUID encounterId);
}
