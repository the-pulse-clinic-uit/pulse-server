package com.pulseclinic.pulse_server.modules.pharmacy.repository;

import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
