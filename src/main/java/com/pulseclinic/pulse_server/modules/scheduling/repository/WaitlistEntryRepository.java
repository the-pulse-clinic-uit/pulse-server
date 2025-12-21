package com.pulseclinic.pulse_server.modules.scheduling.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.enums.WaitlistStatus;
import com.pulseclinic.pulse_server.modules.scheduling.entity.WaitlistEntry;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, UUID> {
    List<WaitlistEntry> findByDoctorIdAndDutyDateAndStatus(UUID doctorId, LocalDate dutyDate, WaitlistStatus status);
    
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.id = :doctorId AND we.dutyDate = :dutyDate AND we.status = :status ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findNextInQueue(@Param("doctorId") UUID doctorId, @Param("dutyDate") LocalDate dutyDate, @Param("status") WaitlistStatus status);
    
    @Query("SELECT COUNT(we) FROM WaitlistEntry we WHERE we.doctor.id = :doctorId AND we.dutyDate = :dutyDate AND we.status = 'WAITING'")
    Integer countWaiting(@Param("doctorId") UUID doctorId, @Param("dutyDate") LocalDate dutyDate);
    
    // Query by department instead of doctor
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.department.id = :departmentId AND we.dutyDate = :dutyDate AND we.status = :status ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findNextInQueueByDepartment(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate, @Param("status") WaitlistStatus status);
    
    @Query("SELECT COUNT(we) FROM WaitlistEntry we WHERE we.doctor.department.id = :departmentId AND we.dutyDate = :dutyDate AND we.status = 'WAITING'")
    Integer countWaitingByDepartment(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate);
}
