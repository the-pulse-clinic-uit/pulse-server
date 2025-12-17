package com.pulseclinic.pulse_server.modules.appointments.repository;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByPatientIdAndStatusAndDeletedAtIsNull(UUID patientId, AppointmentStatus status);
    
    List<Appointment> findByDoctorIdAndStartsAtBetweenAndDeletedAtIsNull(UUID doctorId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.patient.id = :patientId AND a.startsAt = :startTime AND a.status != 'CANCELLED' AND a.deletedAt IS NULL")
    List<Appointment> findConflicts(@Param("doctorId") UUID doctorId, @Param("patientId") UUID patientId, @Param("startTime") LocalDateTime startTime);

    List<Appointment> findByPatientIdAndDeletedAtIsNullOrderByStartsAtDesc(UUID patientId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.startsAt > :now AND a.deletedAt IS NULL ORDER BY a.startsAt ASC")
    List<Appointment> findUpcomingByPatient(@Param("patientId") UUID patientId, @Param("now") LocalDateTime now);
}
