package com.pulseclinic.pulse_server.modules.encounters.repository;

import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowUpPlanRepository extends JpaRepository<FollowUpPlan, UUID> {
}
