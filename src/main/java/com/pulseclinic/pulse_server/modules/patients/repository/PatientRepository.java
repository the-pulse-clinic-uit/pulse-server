package com.pulseclinic.pulse_server.modules.patients.repository;

import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query("SELECT p FROM Patient p WHERE p.user.citizenId = :citizenId")
    Optional<Patient> findByUserCitizenId(@Param("citizenId") String citizenId);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.user.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Patient> findByUserFullNameContaining(@Param("fullName") String fullName);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.user.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<Patient> findByUserEmailContaining(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.user = :user")
    Optional<Patient> findByUser(@Param("user") User user);

    @Query("SELECT p FROM Patient p WHERE p.user.email = :email AND p.user.deletedAt IS NULL")
    Optional<Patient> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.user.deletedAt IS NULL")
    List<Patient> findAll();

    // For reports
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
