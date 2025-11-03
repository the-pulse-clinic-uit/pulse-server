package com.pulseclinic.pulse_server.modules.patients.repository;

import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
