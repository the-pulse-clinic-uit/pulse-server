package com.pulseclinic.pulse_server.modules.admissions.repository;

import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, UUID> {
}
