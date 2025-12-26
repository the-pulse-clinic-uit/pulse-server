package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // Create new prescription from encounter
    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto) {
        try {
            PrescriptionDto prescription = prescriptionService.createPrescription(prescriptionRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get prescription by ID
    @GetMapping("/{prescriptionId}")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable UUID prescriptionId) {
        java.util.Optional<PrescriptionDto> prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return prescription.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get prescription details (drug items)
    @GetMapping("/{prescriptionId}/details")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<List<PrescriptionDetailDto>> getDetails(@PathVariable UUID prescriptionId) {
        List<PrescriptionDetailDto> details = prescriptionService.getDetails(prescriptionId);
        return ResponseEntity.ok(details);
    }

    // Finalize prescription (DRAFT -> FINAL)
    @PutMapping("/{prescriptionId}/finalize")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> finalizePrescription(@PathVariable UUID prescriptionId) {
        boolean finalized = prescriptionService.finalizePrescription(prescriptionId);
        return finalized ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Dispense medication (FINAL -> DISPENSED)
    @PutMapping("/{prescriptionId}/dispense")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> dispenseMedication(@PathVariable UUID prescriptionId) {
        boolean dispensed = prescriptionService.dispenseMedication(prescriptionId);
        return dispensed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Calculate prescription total
    @GetMapping("/{prescriptionId}/total")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<BigDecimal> calculateTotal(@PathVariable UUID prescriptionId) {
        BigDecimal total = prescriptionService.calculateTotal(prescriptionId);
        return ResponseEntity.ok(total);
    }

    // Print prescription
    @GetMapping("/{prescriptionId}/print")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<String> printPrescription(@PathVariable UUID prescriptionId) {
        String printout = prescriptionService.printPrescription(prescriptionId);
        return ResponseEntity.ok(printout);
    }
}
