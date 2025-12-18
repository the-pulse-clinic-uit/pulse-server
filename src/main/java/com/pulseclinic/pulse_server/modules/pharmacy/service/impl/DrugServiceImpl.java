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
        return this.drugRepository.save(drug);
    }
}
