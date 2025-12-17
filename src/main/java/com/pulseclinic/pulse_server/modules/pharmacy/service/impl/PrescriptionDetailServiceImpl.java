package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import com.pulseclinic.pulse_server.mappers.impl.PrescriptionDetailMapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionDetailRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrescriptionDetailServiceImpl implements PrescriptionDetailService {

    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DrugRepository drugRepository;
    private final PrescriptionDetailMapper prescriptionDetailMapper;

    public PrescriptionDetailServiceImpl(PrescriptionDetailRepository prescriptionDetailRepository,
                                        PrescriptionRepository prescriptionRepository,
                                        DrugRepository drugRepository,
                                        PrescriptionDetailMapper prescriptionDetailMapper) {
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.drugRepository = drugRepository;
        this.prescriptionDetailMapper = prescriptionDetailMapper;
    }

    @Override
    @Transactional
    public PrescriptionDetailDto createDetail(UUID prescriptionId, PrescriptionDetailRequestDto detailRequestDto) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(prescriptionId);
        if (prescriptionOpt.isEmpty()) {
            throw new RuntimeException("Prescription not found");
        }

        Optional<Drug> drugOpt = drugRepository.findById(detailRequestDto.getDrug_id());
        if (drugOpt.isEmpty()) {
            throw new RuntimeException("Drug not found");
        }

        Drug drug = drugOpt.get();
        BigDecimal unitPrice = detailRequestDto.getUnit_price() != null ? 
                detailRequestDto.getUnit_price() : drug.getUnit_price();
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(detailRequestDto.getQuantity()));

        PrescriptionDetail detail = PrescriptionDetail.builder()
                .strength_text(detailRequestDto.getStrength_text())
                .quantity(detailRequestDto.getQuantity())
                .unit_price(unitPrice)
                .item_total_price(itemTotal)
                .dose(detailRequestDto.getDose())
                .timing(detailRequestDto.getTiming())
                .instructions(detailRequestDto.getInstructions())
                .frequency(detailRequestDto.getFrequency())
                .drug(drug)
                .prescription(prescriptionOpt.get())
                .build();

        PrescriptionDetail savedDetail = prescriptionDetailRepository.save(detail);
        return prescriptionDetailMapper.mapTo(savedDetail);
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
    @Transactional
    public boolean updateQuantity(UUID detailId, Integer quantity) {
        try {
            Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
            if (detailOpt.isEmpty()) {
                return false;
            }

            PrescriptionDetail detail = detailOpt.get();
            detail.setQuantity(quantity);
            
            updateLineTotal(detailId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateLineTotal(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        PrescriptionDetail detail = detailOpt.get();
        return detail.getUnit_price().multiply(BigDecimal.valueOf(detail.getQuantity()));
    }

    @Override
    @Transactional
    public void updateLineTotal(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return;
        }

        PrescriptionDetail detail = detailOpt.get();
        BigDecimal lineTotal = calculateLineTotal(detailId);
        detail.setItem_total_price(lineTotal);
        prescriptionDetailRepository.save(detail);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFormattedDosage(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return "";
        }

        PrescriptionDetail detail = detailOpt.get();
        return detail.getDose() + " - " + detail.getFrequency() + " - " + detail.getTiming();
    }

    @Override
    @Transactional(readOnly = true)
    public String getFormattedInstructions(UUID detailId) {
        Optional<PrescriptionDetail> detailOpt = prescriptionDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return "";
        }

        PrescriptionDetail detail = detailOpt.get();
        StringBuilder sb = new StringBuilder();

        sb.append(detail.getDrug().getName()).append("\n");
        sb.append("Dosage: ").append(detail.getDose()).append("\n");
        sb.append("Frequency: ").append(detail.getFrequency()).append("\n");
        sb.append("Timing: ").append(detail.getTiming()).append("\n");
        sb.append("Instructions: ").append(detail.getInstructions());

        return sb.toString();
    }
}
