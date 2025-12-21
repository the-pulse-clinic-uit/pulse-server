package com.pulseclinic.pulse_server.modules.encounters.repository;

import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowUpPlanRepository extends JpaRepository<FollowUpPlan, UUID> {
    List<FollowUpPlan> findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID patientId);
    List<FollowUpPlan> findByDoctorIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID doctorId);
    List<FollowUpPlan> findByBaseEncounterIdAndDeletedAtIsNull(UUID encounterId);
    List<FollowUpPlan> findByPatientIdAndStatusAndDeletedAtIsNull(UUID patientId, com.pulseclinic.pulse_server.enums.FollowUpPlanStatus status);
}
