package com.pulseclinic.pulse_server.modules.encounters.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanRequestDto;

public interface FollowUpPlanService {
    FollowUpPlanDto createPlan(FollowUpPlanRequestDto followUpPlanRequestDto);
    FollowUpPlanDto createFromEncounter(UUID encounterId, FollowUpPlanRequestDto followUpPlanRequestDto);
    Optional<FollowUpPlanDto> getFollowUpPlanById(UUID planId);
    boolean editPlan(UUID planId, FollowUpPlanRequestDto followUpPlanRequestDto);
    boolean pausePlan(UUID planId);
    boolean resumePlan(UUID planId);
    boolean completePlan(UUID planId);
    boolean deletePlan(UUID planId);
    List<AppointmentDto> generateAppointments(UUID planId);
    
    // Query methods
    List<FollowUpPlanDto> getByPatient(UUID patientId);
    List<FollowUpPlanDto> getByDoctor(UUID doctorId);
    List<FollowUpPlanDto> getByPatientAndStatus(UUID patientId, FollowUpPlanStatus status);
    List<FollowUpPlanDto> getUpcomingPlans(LocalDate startDate, LocalDate endDate);
}
