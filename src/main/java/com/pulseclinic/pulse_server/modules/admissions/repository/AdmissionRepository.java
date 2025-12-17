package com.pulseclinic.pulse_server.modules.admissions.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, UUID> {
    List<Admission> findByPatientIdAndDeletedAtIsNullOrderByAdmittedAtDesc(UUID patientId);
    List<Admission> findByDoctorIdAndDeletedAtIsNullOrderByAdmittedAtDesc(UUID doctorId);
    List<Admission> findByRoomIdAndStatusAndDeletedAtIsNull(UUID roomId, com.pulseclinic.pulse_server.enums.AdmissionStatus status);
    List<Admission> findByStatusAndDeletedAtIsNull(com.pulseclinic.pulse_server.enums.AdmissionStatus status);
    Optional<Admission> findByPatientIdAndStatusAndDeletedAtIsNull(UUID patientId, com.pulseclinic.pulse_server.enums.AdmissionStatus status);
    Optional<Admission> findByEncounterIdAndDeletedAtIsNull(UUID encounterId);
}
