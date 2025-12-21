package com.pulseclinic.pulse_server.modules.scheduling.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, UUID> {
    List<ShiftAssignment> findByDutyDate(LocalDate dutyDate);
    
    List<ShiftAssignment> findByDoctorIdAndDutyDateBetween(UUID doctorId, LocalDate startDate, LocalDate endDate);
    
    List<ShiftAssignment> findByDoctorIdAndDutyDate(UUID doctorId, LocalDate dutyDate);
    
    List<ShiftAssignment> findByShiftIdAndDutyDate(UUID shiftId, LocalDate dutyDate);
    
    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.doctor.id = :doctorId AND sa.shift.id = :shiftId AND sa.dutyDate = :dutyDate")
    List<ShiftAssignment> findConflicts(@Param("doctorId") UUID doctorId, @Param("shiftId") UUID shiftId, @Param("dutyDate") LocalDate dutyDate);
    
    List<ShiftAssignment> findByDoctorIdAndStatus(UUID doctorId, ShiftAssignmentStatus status);

    boolean existsByDoctorIdAndShiftIdAndDutyDate(UUID id, UUID id1, @NotNull(message = "Duty Date is required") LocalDate dutyDate);
}
