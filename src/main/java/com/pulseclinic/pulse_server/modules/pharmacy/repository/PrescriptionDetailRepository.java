package com.pulseclinic.pulse_server.modules.pharmacy.repository;

import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, UUID> {
    List<PrescriptionDetail> findByPrescriptionIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID prescriptionId);
    void deleteByPrescriptionId(UUID prescriptionId);
}
