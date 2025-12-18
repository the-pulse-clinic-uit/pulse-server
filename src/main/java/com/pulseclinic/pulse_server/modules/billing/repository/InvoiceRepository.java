package com.pulseclinic.pulse_server.modules.billing.repository;

import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByEncounterIdAndDeletedAtIsNull(UUID encounterId);
    List<Invoice> findByStatusAndDeletedAtIsNull(com.pulseclinic.pulse_server.enums.InvoiceStatus status);
    @Query("SELECT i FROM Invoice i WHERE i.encounter.patient.id = :patientId AND i.deletedAt IS NULL ORDER BY i.createdAt DESC")
    List<Invoice> findByPatientId(@Param("patientId") UUID patientId);
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :date AND i.deletedAt IS NULL")
    List<Invoice> findOverdueInvoices(@Param("status") com.pulseclinic.pulse_server.enums.InvoiceStatus status, @Param("date") java.time.LocalDate date);
}
