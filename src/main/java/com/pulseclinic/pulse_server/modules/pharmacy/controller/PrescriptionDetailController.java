package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionDetailService;

import jakarta.validation.Valid;
@Slf4j
@RestController
@RequestMapping("/prescriptions/details")
public class PrescriptionDetailController {
    private final PrescriptionDetailService prescriptionDetailService;

    public PrescriptionDetailController(PrescriptionDetailService prescriptionDetailService) {
        this.prescriptionDetailService = prescriptionDetailService;
    }

    // Add drug item to prescription
    @PostMapping
    public ResponseEntity<PrescriptionDetailDto> createDetail(
            @Valid @RequestBody PrescriptionDetailRequestDto detailRequestDto) {
        try {
            PrescriptionDetailDto detail = prescriptionDetailService.createDetail(detailRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(detail);
        } catch (Exception e) {
            log.info("Error: {}", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get prescription detail by ID
    @GetMapping("/{itemId}")
    public ResponseEntity<PrescriptionDetailDto> getDetailById(@PathVariable UUID itemId) {
        java.util.Optional<PrescriptionDetailDto> detail = prescriptionDetailService.getDetailById(itemId);
        return detail.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update dosage information
    @PutMapping("/{itemId}/dosage")
    public ResponseEntity<Void> updateDosage(
            @PathVariable UUID itemId,
            @RequestParam String dose,
            @RequestParam String frequency,
            @RequestParam String timing) {
        boolean updated = prescriptionDetailService.updateDosage(itemId, dose, frequency, timing);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Update quantity
    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        boolean updated = prescriptionDetailService.updateQuantity(itemId, quantity);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Remove drug item from prescription
    @org.springframework.web.bind.annotation.DeleteMapping("/details/{itemId}")
    public ResponseEntity<Void> removeDrugItem(@PathVariable UUID itemId) {
        boolean removed = prescriptionDetailService.removeDrugItem(itemId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
