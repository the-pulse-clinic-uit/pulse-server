package com.pulseclinic.pulse_server.modules.pharmacy.repository;

import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByEncounterIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID encounterId);
    Optional<Prescription> findByEncounterIdAndDeletedAtIsNull(UUID encounterId);
    List<Prescription> findByStatusAndDeletedAtIsNull(com.pulseclinic.pulse_server.enums.PrescriptionStatus status);

    @Query("SELECT p FROM Prescription p WHERE p.encounter.patient.id = :patientId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Prescription> findByPatientId(@Param("patientId") UUID patientId);
}
