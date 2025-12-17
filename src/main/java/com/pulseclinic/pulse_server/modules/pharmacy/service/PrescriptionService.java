package com.pulseclinic.pulse_server.modules.pharmacy.service;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PrescriptionService {
    PrescriptionDto createPrescription(UUID encounterId, PrescriptionRequestDto prescriptionRequestDto);
    PrescriptionDetailDto addDrugItem(UUID prescriptionId, PrescriptionDetailRequestDto detailRequestDto);
    boolean removeDrugItem(UUID prescriptionId, UUID detailId);
    boolean finalizePrescription(UUID prescriptionId);
    boolean dispenseMedication(UUID prescriptionId);
    BigDecimal calculateTotal(UUID prescriptionId);
    void updateTotal(UUID prescriptionId);
    List<PrescriptionDetailDto> getDetails(UUID prescriptionId);
    String printPrescription(UUID prescriptionId);
    boolean canModify(UUID prescriptionId);
    boolean canDispense(UUID prescriptionId);
    boolean isDraft(UUID prescriptionId);
    boolean isFinal(UUID prescriptionId);
    boolean isDispensed(UUID prescriptionId);
}
