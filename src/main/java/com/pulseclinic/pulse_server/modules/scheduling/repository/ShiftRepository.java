package com.pulseclinic.pulse_server.modules.scheduling.repository;

import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    Optional<Shift> findByName(String name);
    boolean existsByName(String name);
}
