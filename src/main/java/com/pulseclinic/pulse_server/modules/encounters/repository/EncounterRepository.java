package com.pulseclinic.pulse_server.modules.encounters.repository;

import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, UUID> {
    List<Encounter> findByPatientIdAndDeletedAtIsNullOrderByStartedAtDesc(UUID patientId);
    List<Encounter> findByDoctorIdAndDeletedAtIsNullOrderByStartedAtDesc(UUID doctorId);
    List<Encounter> findByPatientIdAndDoctorIdAndDeletedAtIsNull(UUID patientId, UUID doctorId);
    Optional<Encounter> findByAppointmentIdAndDeletedAtIsNull(UUID appointmentId);
}
