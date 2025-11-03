package com.pulseclinic.pulse_server.modules.staff.repository;

import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}
