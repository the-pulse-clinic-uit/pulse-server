package com.pulseclinic.pulse_server.modules.appointments.repository;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.AppointmentType;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
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

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.patient.id = :patientId
              AND a.startsAt = :startTime
              AND a.status <> :cancelled
              AND a.deletedAt IS NULL
            """)
    List<Appointment> findConflicts(@Param("doctorId") UUID doctorId, @Param("patientId") UUID patientId, @Param("startTime") LocalDateTime startTime);

    List<Appointment> findByPatientIdAndDeletedAtIsNullOrderByStartsAtDesc(UUID patientId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.patient.id = :patientId
              AND a.startsAt > :now
              AND a.deletedAt IS NULL
            ORDER BY a.startsAt ASC
            """)
    List<Appointment> findUpcomingByPatient(@Param("patientId") UUID patientId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.department = :department AND a.deletedAt IS NULL")
    Integer countByDoctorDepartment(@Param("department") Department department);

    // Report query methods
    Long countByStartsAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByStatusAndStartsAtBetween(AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    Long countByTypeAndStartsAtBetween(AppointmentType type, LocalDateTime start, LocalDateTime end);

    Long countByDoctorIdAndStartsAtBetween(UUID doctorId, LocalDateTime start, LocalDateTime end);

    Long countByDoctorIdAndStatusAndStartsAtBetween(UUID doctorId, AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.department.id = :departmentId AND a.startsAt BETWEEN :start AND :end AND a.deletedAt IS NULL")
    Long countByDoctorDepartmentIdAndStartsAtBetween(@Param("departmentId") UUID departmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.department.id = :departmentId AND a.status = :status AND a.startsAt BETWEEN :start AND :end AND a.deletedAt IS NULL")
    Long countByDoctorDepartmentIdAndStatusAndStartsAtBetween(@Param("departmentId") UUID departmentId, @Param("status") AppointmentStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
