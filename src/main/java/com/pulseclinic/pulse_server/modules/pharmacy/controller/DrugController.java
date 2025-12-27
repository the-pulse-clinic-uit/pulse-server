package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import com.pulseclinic.pulse_server.mappers.impl.DrugMapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.service.DrugService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drugs")
public class DrugController {
    private final DrugService drugService;
    private final DrugMapper drugMapper;

    public DrugController(DrugService drugService, DrugMapper drugMapper) {
        this.drugService = drugService;
        this.drugMapper = drugMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('staff', 'doctor')")
    public ResponseEntity<DrugDto> createDrug(@RequestBody DrugRequestDto drugRequestDto) {
        Drug drug = this.drugService.createDrug(this.drugMapper.mapFrom(drugRequestDto));
        return new ResponseEntity<>(drugMapper.mapTo(drug) ,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugDto> getDrug(@PathVariable UUID id) {
        Optional<Drug> drug = this.drugService.findById(id);
        if (drug.isPresent()) {
            return new ResponseEntity<>(this.drugMapper.mapTo(drug.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<DrugDto>> getAllDrugs() {
        List<Drug> drugs = this.drugService.getAllDrugs();
        return new ResponseEntity<>(drugs.stream().map(d -> this.drugMapper.mapTo(d)).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<DrugDto> updateDrug(@PathVariable UUID id, @RequestBody DrugDto drugDto) {
        Drug drug = this.drugService.updateDrug(id, drugDto);
        return new ResponseEntity<>(this.drugMapper.mapTo(drug), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<HttpStatus> deleteDrug(@PathVariable UUID id) {
        this.drugService.deleteDrug(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
