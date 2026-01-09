package com.pulseclinic.pulse_server.modules.pharmacy.service;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DrugService {
    Drug createDrug(Drug drug);
    Optional<Drug> findById(UUID id);
    List<Drug> getAllDrugs();
    void deleteDrug(UUID id);
    Drug updateDrug(UUID id, DrugDto drugDto);
    void deductStock(UUID drugId, Integer quantity);
    void restockDrug(UUID drugId, Integer quantity);
    boolean hasAvailableStock(UUID drugId, Integer requestedQuantity);
}
