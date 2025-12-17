package com.pulseclinic.pulse_server.modules.pharmacy.service;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PrescriptionDetailService {
    PrescriptionDetailDto createDetail(UUID prescriptionId, PrescriptionDetailRequestDto detailRequestDto);
    boolean updateDosage(UUID detailId, String dose, String frequency, String timing);
    boolean updateQuantity(UUID detailId, Integer quantity);
    BigDecimal calculateLineTotal(UUID detailId);
    void updateLineTotal(UUID detailId);
    String getFormattedDosage(UUID detailId);
    String getFormattedInstructions(UUID detailId);
}
