package com.pulseclinic.pulse_server.modules.pharmacy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;

public interface PrescriptionService {
    PrescriptionDto createPrescription(PrescriptionRequestDto prescriptionRequestDto);
    Optional<PrescriptionDto> getPrescriptionById(UUID prescriptionId);
    List<PrescriptionDetailDto> getDetails(UUID prescriptionId);
    boolean finalizePrescription(UUID prescriptionId);
    boolean dispenseMedication(UUID prescriptionId);
    BigDecimal calculateTotal(UUID prescriptionId);
    String printPrescription(UUID prescriptionId);
}
