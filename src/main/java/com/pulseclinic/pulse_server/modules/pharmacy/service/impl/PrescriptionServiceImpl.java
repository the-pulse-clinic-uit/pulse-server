package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    @Override
    public PrescriptionDto createPrescription(UUID encounterId, PrescriptionRequestDto prescriptionRequestDto) {
        return null;
    }

    @Override
    public PrescriptionDetailDto addDrugItem(UUID prescriptionId, PrescriptionDetailRequestDto detailRequestDto) {
        return null;
    }

    @Override
    public boolean removeDrugItem(UUID prescriptionId, UUID detailId) {
        return false;
    }

    @Override
    public boolean finalizePrescription(UUID prescriptionId) {
        return false;
    }

    @Override
    public boolean dispenseMedication(UUID prescriptionId) {
        return false;
    }

    @Override
    public BigDecimal calculateTotal(UUID prescriptionId) {
        return null;
    }

    @Override
    public void updateTotal(UUID prescriptionId) {

    }

    @Override
    public List<PrescriptionDetailDto> getDetails(UUID prescriptionId) {
        return List.of();
    }

    @Override
    public String printPrescription(UUID prescriptionId) {
        return "";
    }

    @Override
    public boolean canModify(UUID prescriptionId) {
        return false;
    }

    @Override
    public boolean canDispense(UUID prescriptionId) {
        return false;
    }

    @Override
    public boolean isDraft(UUID prescriptionId) {
        return false;
    }

    @Override
    public boolean isFinal(UUID prescriptionId) {
        return false;
    }

    @Override
    public boolean isDispensed(UUID prescriptionId) {
        return false;
    }
}
