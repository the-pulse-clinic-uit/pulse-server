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

    // Find entries by doctor, date and status
    List<WaitlistEntry> findByDoctorIdAndDutyDateAndStatus(UUID doctorId, LocalDate dutyDate, WaitlistStatus status);

    // Find next in queue for a specific doctor (ordered by priority DESC, then createdAt ASC)
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.id = :doctorId AND we.dutyDate = :dutyDate AND we.status = :status ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findNextInQueue(@Param("doctorId") UUID doctorId, @Param("dutyDate") LocalDate dutyDate, @Param("status") WaitlistStatus status);

    // Count waiting entries for a specific doctor
    @Query("SELECT COUNT(we) FROM WaitlistEntry we WHERE we.doctor.id = :doctorId AND we.dutyDate = :dutyDate AND we.status = 'WAITING'")
    Integer countWaiting(@Param("doctorId") UUID doctorId, @Param("dutyDate") LocalDate dutyDate);

    // Find next in queue by DEPARTMENT (ordered by priority DESC, then createdAt ASC)
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId AND we.dutyDate = :dutyDate AND we.status = :status ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findNextInQueueByDepartment(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate, @Param("status") WaitlistStatus status);

    // Count waiting entries by DEPARTMENT
    @Query("SELECT COUNT(we) FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId AND we.dutyDate = :dutyDate AND we.status = 'WAITING'")
    Integer countWaitingByDepartment(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate);

    // Find maximum ticket number for a department on a specific date (for ticket generation)
    @Query("SELECT MAX(we.ticketNo) FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId AND we.dutyDate = :dutyDate")
    Integer findMaxTicketNoByDepartment(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate);

    // Find all entries by DEPARTMENT and DUTY DATE (ordered by priority DESC, then createdAt ASC)
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId AND we.dutyDate = :dutyDate ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findByDepartmentAndDutyDate(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate);

    // Find all entries by DEPARTMENT, DUTY DATE and STATUS (ordered by priority DESC, then createdAt ASC)
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId AND we.dutyDate = :dutyDate AND we.status = :status ORDER BY we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findByDepartmentAndDutyDateAndStatus(@Param("departmentId") UUID departmentId, @Param("dutyDate") LocalDate dutyDate, @Param("status") WaitlistStatus status);

    // Find all entries by DEPARTMENT only (for historical data or reports)
    @Query("SELECT we FROM WaitlistEntry we WHERE we.doctor.staff.department.id = :departmentId ORDER BY we.dutyDate DESC, we.priority DESC, we.createdAt ASC")
    List<WaitlistEntry> findByDepartment(@Param("departmentId") UUID departmentId);
}