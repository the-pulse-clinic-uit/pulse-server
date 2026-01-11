package com.pulseclinic.pulse_server.modules.pharmacy.service.impl;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.service.DrugService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DrugServiceImpl implements DrugService {
    private final DrugRepository drugRepository;

    public DrugServiceImpl(DrugRepository drugRepository){
        this.drugRepository = drugRepository;
    }

    @Override
    public Drug createDrug(Drug drug) {
        return this.drugRepository.save(drug);
    }

    @Override
    public Optional<Drug> findById(UUID id) {
        return this.drugRepository.findById(id);
    }

    @Override
    public List<Drug> getAllDrugs() {
        return this.drugRepository.findAll();
    }

    @Override
    public void deleteDrug(UUID id) {
        Drug drug = this.drugRepository.findById(id).orElseThrow(() -> new RuntimeException("Drug not found"));
        drug.setDeletedAt(LocalDateTime.now());
        this.drugRepository.save(drug);
    }

    @Override
    public Drug updateDrug(UUID id, DrugDto drugDto) {
        Drug drug = this.drugRepository.findById(id).orElseThrow(() -> new RuntimeException("Drug not found"));
        if (drugDto.getName() != null){
            drug.setName(drugDto.getName());
        }
        if (drugDto.getUnitPrice() != null){
            drug.setUnitPrice(drugDto.getUnitPrice());
        }
        if (drugDto.getUnit() != null){
            drug.setUnit(drugDto.getUnit());
        }
        if (drugDto.getStrength() != null){
            drug.setStrength(drugDto.getStrength());
        }
        if (drugDto.getDosageForm() != null){
            drug.setDosageForm(drugDto.getDosageForm());
        }
        if (drugDto.getQuantity() != null){
            drug.setQuantity(drugDto.getQuantity());
        }
        if (drugDto.getExpiryDate() != null){
            drug.setExpiryDate(drugDto.getExpiryDate());
        }
        if (drugDto.getMinStockLevel() != null){
            drug.setMinStockLevel(drugDto.getMinStockLevel());
        }
        if (drugDto.getBatchNumber() != null){
            drug.setBatchNumber(drugDto.getBatchNumber());
        }
        return this.drugRepository.save(drug);
    }

    @Override
    public void deductStock(UUID drugId, Integer quantity) {
        Drug drug = this.drugRepository.findById(drugId)
                .orElseThrow(() -> new RuntimeException("Drug not found"));

        if (drug.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for drug: " + drug.getName() +
                    ". Available: " + drug.getQuantity() + ", Required: " + quantity);
        }

        drug.setQuantity(drug.getQuantity() - quantity);
        this.drugRepository.save(drug);
    }

    @Override
    public void restockDrug(UUID drugId, Integer quantity) {
        Drug drug = this.drugRepository.findById(drugId)
                .orElseThrow(() -> new RuntimeException("Drug not found"));

        drug.setQuantity(drug.getQuantity() + quantity);
        this.drugRepository.save(drug);
    }

    @Override
    public boolean hasAvailableStock(UUID drugId, Integer requestedQuantity) {
        Drug drug = this.drugRepository.findById(drugId)
                    .orElseThrow(() -> new RuntimeException("Drug not found"));

        return drug.getQuantity() >= requestedQuantity;
    }
}
