package com.pulseclinic.pulse_server.modules.encounters.repository;

import com.pulseclinic.pulse_server.enums.EncounterType;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, UUID> {
    List<Encounter> findByPatientIdAndDeletedAtIsNullOrderByStartedAtDesc(UUID patientId);
    List<Encounter> findByDoctorIdAndDeletedAtIsNullOrderByStartedAtDesc(UUID doctorId);
    List<Encounter> findByPatientIdAndDoctorIdAndDeletedAtIsNull(UUID patientId, UUID doctorId);
    Optional<Encounter> findByAppointmentIdAndDeletedAtIsNull(UUID appointmentId);
    boolean existsByAppointmentId(UUID appointmentId);

    List<Encounter> findByDeletedAtIsNullOrderByStartedAtDesc();
    List<Encounter> findByTypeAndDeletedAtIsNullOrderByStartedAtDesc(EncounterType type);
    List<Encounter> findByEndedAtIsNullAndDeletedAtIsNullOrderByStartedAtDesc();
    List<Encounter> findByEndedAtIsNotNullAndDeletedAtIsNullOrderByEndedAtDesc();

    @Query("""
        SELECT e FROM Encounter e
        WHERE e.startedAt BETWEEN :startDate AND :endDate
          AND e.deletedAt IS NULL
        ORDER BY e.startedAt ASC
        """)
    List<Encounter> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT e FROM Encounter e
        WHERE FUNCTION('DATE', e.startedAt) = CURRENT_DATE
          AND e.deletedAt IS NULL
        ORDER BY e.startedAt ASC
        """)
    List<Encounter> findTodayEncounters();

    List<Encounter> findByDoctorIdAndRatingIsNotNullAndDeletedAtIsNullOrderByRatedAtDesc(UUID doctorId);
}
