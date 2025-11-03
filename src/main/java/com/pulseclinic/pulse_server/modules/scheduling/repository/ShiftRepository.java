package com.pulseclinic.pulse_server.modules.scheduling.repository;

import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
}
