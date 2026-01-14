package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import com.pulseclinic.pulse_server.mappers.impl.PrescriptionDetailMapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyWarning;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionDetailRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.service.AllergyCheckService;
import com.pulseclinic.pulse_server.modules.pharmacy.service.DrugService;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PrescriptionDetailServiceImpl implements PrescriptionDetailService {

    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DrugRepository drugRepository;
    private final PrescriptionDetailMapper prescriptionDetailMapper;
    private final DrugService drugService;
    private final AllergyCheckService allergyCheckService;

    public PrescriptionDetailServiceImpl(PrescriptionDetailRepository prescriptionDetailRepository,
            PrescriptionRepository prescriptionRepository,
            DrugRepository drugRepository,
            PrescriptionDetailMapper prescriptionDetailMapper,
            DrugService drugService,
            AllergyCheckService allergyCheckService) {
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.drugRepository = drugRepository;
        this.prescriptionDetailMapper = prescriptionDetailMapper;
        this.drugService = drugService;
        this.allergyCheckService = allergyCheckService;
    }

    @Override
    @Transactional
    public PrescriptionDetailDto createDetail(PrescriptionDetailRequestDto detailRequestDto) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(detailRequestDto.getPrescriptionId());
        if (prescriptionOpt.isEmpty()) {
            throw new RuntimeException("Prescription not found");
        }

        Optional<Drug> drugOpt = drugRepository.findById(detailRequestDto.getDrugId());
        if (drugOpt.isEmpty()) {
            throw new RuntimeException("Drug not found");
        }

        Drug drug = drugOpt.get();
        Prescription prescription = prescriptionOpt.get();

        // Check for allergies BEFORE saving
        UUID patientId = prescription.getEncounter().getPatient().getId();
        List<AllergyWarning> allergyWarnings = allergyCheckService.checkPatientAllergies(
                patientId,
                Collections.singletonList(drug.getId()));

        // Log warnings (don't block - doctor can override)
        if (!allergyWarnings.isEmpty()) {
            for (AllergyWarning warning : allergyWarnings) {
                log.warn("ALLERGY WARNING for prescription {}: {}",
                        prescription.getId(), warning.getMessage());
            }
        }

        if (!drugService.hasAvailableStock(drug.getId(), detailRequestDto.getQuantity())) {
            throw new RuntimeException("Insufficient stock for drug: " + drug.getName() +
                    ". Available: " + drug.getQuantity() + ", Requested: " + detailRequestDto.getQuantity());
        }

        drugService.deductStock(drug.getId(), detailRequestDto.getQuantity());

        BigDecimal unitPrice = detailRequestDto.getUnitPrice() != null ? detailRequestDto.getUnitPrice()
                : drug.getUnitPrice();
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(detailRequestDto.getQuantity()));

        PrescriptionDetail detail = PrescriptionDetail.builder()
                .strengthText(detailRequestDto.getStrength_text())
                .quantity(detailRequestDto.getQuantity())
                .unitPrice(unitPrice)
                .itemTotalPrice(itemTotal)
                .dose(detailRequestDto.getDose())
                .timing(detailRequestDto.getTiming())
                .instructions(detailRequestDto.getInstructions())
                .frequency(detailRequestDto.getFrequency())
                .drug(drug)
                .prescription(prescriptionOpt.get())
                .build();

        PrescriptionDetail savedDetail = prescriptionDetailRepository.save(detail);

        // Add allergy warnings to the response
        PrescriptionDetailDto responseDto = prescriptionDetailMapper.mapTo(savedDetail);
        responseDto.setAllergyWarnings(allergyWarnings);

        return responseDto;
    }

    @Override
    @Transactional
    public boolean updateDosage(UUID detailId, String dose, String frequency, String timing) {
        try {
            Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
            if (detailOpt.isEmpty()) {
                return false;
            }

            PrescriptionDetail detail = detailOpt.get();
            detail.setDose(dose);
            detail.setFrequency(frequency);
            detail.setTiming(timing);
            prescriptionDetailRepository.save(detail);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrescriptionDetailDto> getDetailById(UUID detailId) {
        return prescriptionDetailRepository.findById(detailId)
                .map(prescriptionDetailMapper::mapTo);
    }

    @Override
    @Transactional
    public boolean updateQuantity(UUID detailId, Integer quantity) {
        try {
            Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
            if (detailOpt.isEmpty()) {
                return false;
            }

            PrescriptionDetail detail = detailOpt.get();
            Integer oldQuantity = detail.getQuantity();
            Integer quantityDiff = quantity - oldQuantity;

            if (quantityDiff > 0) {
                if (!drugService.hasAvailableStock(detail.getDrug().getId(), quantityDiff)) {
                    throw new RuntimeException("Insufficient stock to increase quantity");
                }
                drugService.deductStock(detail.getDrug().getId(), quantityDiff);
            } else if (quantityDiff < 0) {
                drugService.restockDrug(detail.getDrug().getId(), Math.abs(quantityDiff));
            }

            detail.setQuantity(quantity);
            updateLineTotal(detailId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeDrugItem(UUID detailId) {
        try {
            Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
            if (detailOpt.isEmpty()) {
                return false;
            }

            PrescriptionDetail detail = detailOpt.get();

            if (detail.getPrescription().getStatus() != com.pulseclinic.pulse_server.enums.PrescriptionStatus.DRAFT) {
                return false;
            }

            drugService.restockDrug(detail.getDrug().getId(), detail.getQuantity());

            prescriptionDetailRepository.deleteById(detailId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public String getFormattedDosage(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return "";
        }

        PrescriptionDetail detail = detailOpt.get();
        return detail.getDose() + " - " + detail.getFrequency() + " - " + detail.getTiming();
    }

    private BigDecimal calculateLineTotal(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        PrescriptionDetail detail = detailOpt.get();
        return detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
    }

    private void updateLineTotal(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return;
        }

        PrescriptionDetail detail = detailOpt.get();
        BigDecimal lineTotal = calculateLineTotal(detailId);
        detail.setItemTotalPrice(lineTotal);
        prescriptionDetailRepository.save(detail);
    }
}
