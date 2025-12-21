package com.pulseclinic.pulse_server.modules.pharmacy.service;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionDetailService {
    PrescriptionDetailDto createDetail(UUID prescriptionId, PrescriptionDetailRequestDto detailRequestDto);
    Optional<PrescriptionDetailDto> getDetailById(UUID detailId);
    boolean updateDosage(UUID detailId, String dose, String frequency, String timing);
    boolean updateQuantity(UUID detailId, Integer quantity);
    boolean removeDrugItem(UUID detailId);
}
