package com.pulseclinic.pulse_server.modules.admissions.repository;

import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, UUID> {
    List<Admission> findByPatientIdOrderByAdmittedAtDesc(UUID patientId);
    List<Admission> findByDoctorIdOrderByAdmittedAtDesc(UUID doctorId);
    List<Admission> findByRoomIdAndStatus(UUID roomId, com.pulseclinic.pulse_server.enums.AdmissionStatus status);
    List<Admission> findByStatus(com.pulseclinic.pulse_server.enums.AdmissionStatus status);
    Optional<Admission> findByPatientIdAndStatus(UUID patientId, com.pulseclinic.pulse_server.enums.AdmissionStatus status);
}
