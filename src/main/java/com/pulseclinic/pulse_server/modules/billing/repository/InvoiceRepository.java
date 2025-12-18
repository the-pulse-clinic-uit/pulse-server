package com.pulseclinic.pulse_server.modules.billing.repository;

import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.encounter.doctor.department = :department AND i.deletedAt IS NULL")
    BigDecimal sumTotalAmountByDoctorDepartment(@Param("department") Department department);
}
