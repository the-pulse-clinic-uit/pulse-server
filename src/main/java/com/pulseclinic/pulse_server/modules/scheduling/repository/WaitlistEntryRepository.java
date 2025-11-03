package com.pulseclinic.pulse_server.modules.scheduling.repository;

import com.pulseclinic.pulse_server.modules.scheduling.entity.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, UUID> {
}
