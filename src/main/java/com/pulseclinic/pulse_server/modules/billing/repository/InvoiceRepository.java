package com.pulseclinic.pulse_server.modules.billing.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByEncounterIdAndDeletedAtIsNull(UUID encounterId);
    List<Invoice> findByStatusAndDeletedAtIsNull(com.pulseclinic.pulse_server.enums.InvoiceStatus status);
    @Query("SELECT i FROM Invoice i WHERE i.encounter.patient.id = :patientId AND i.deletedAt IS NULL ORDER BY i.createdAt DESC")
    List<Invoice> findByPatientId(@Param("patientId") UUID patientId);
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :date AND i.deletedAt IS NULL")
    List<Invoice> findOverdueInvoices(@Param("status") com.pulseclinic.pulse_server.enums.InvoiceStatus status, @Param("date") java.time.LocalDate date);
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.encounter.doctor.staff.department = :department AND i.deletedAt IS NULL")
    BigDecimal sumTotalAmountByDoctorDepartment(@Param("department") Department department);

    // Report query methods
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.status != com.pulseclinic.pulse_server.enums.InvoiceStatus.VOID AND i.deletedAt IS NULL")
    BigDecimal sumTotalAmountByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(i.amountPaid), 0) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end AND i.deletedAt IS NULL")
    BigDecimal sumAmountPaidByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(i.totalAmount - i.amountPaid), 0) FROM Invoice i WHERE i.status IN (com.pulseclinic.pulse_server.enums.InvoiceStatus.UNPAID, com.pulseclinic.pulse_server.enums.InvoiceStatus.PARTIAL, com.pulseclinic.pulse_server.enums.InvoiceStatus.OVERDUE) AND i.deletedAt IS NULL")
    BigDecimal sumOutstandingDebt();

    // Revenue by doctor
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.encounter.doctor.id = :doctorId AND i.createdAt BETWEEN :start AND :end AND i.status != com.pulseclinic.pulse_server.enums.InvoiceStatus.VOID AND i.deletedAt IS NULL")
    BigDecimal sumTotalAmountByDoctorIdAndCreatedAtBetween(@Param("doctorId") UUID doctorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(i.amountPaid), 0) FROM Invoice i WHERE i.encounter.doctor.id = :doctorId AND i.createdAt BETWEEN :start AND :end AND i.deletedAt IS NULL")
    BigDecimal sumAmountPaidByDoctorIdAndCreatedAtBetween(@Param("doctorId") UUID doctorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Revenue by department
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.encounter.doctor.staff.department.id = :departmentId AND i.createdAt BETWEEN :start AND :end AND i.status != com.pulseclinic.pulse_server.enums.InvoiceStatus.VOID AND i.deletedAt IS NULL")
    BigDecimal sumTotalAmountByDepartmentIdAndCreatedAtBetween(@Param("departmentId") UUID departmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(i.amountPaid), 0) FROM Invoice i WHERE i.encounter.doctor.staff.department.id = :departmentId AND i.createdAt BETWEEN :start AND :end AND i.deletedAt IS NULL")
    BigDecimal sumAmountPaidByDepartmentIdAndCreatedAtBetween(@Param("departmentId") UUID departmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
