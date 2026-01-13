package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyCheckRequest;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyCheckResponse;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy.AllergyWarning;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionWithDetailsDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.service.AllergyCheckService;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final PatientRepository patientRepository;
    private final AllergyCheckService allergyCheckService;

    public PrescriptionController(PrescriptionService prescriptionService,
            PatientRepository patientRepository,
            AllergyCheckService allergyCheckService) {
        this.prescriptionService = prescriptionService;
        this.patientRepository = patientRepository;
        this.allergyCheckService = allergyCheckService;
    }

    // Create new prescription from encounter
    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @Valid @RequestBody PrescriptionRequestDto prescriptionRequestDto) {
        try {
            PrescriptionDto prescription = prescriptionService.createPrescription(prescriptionRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('patient')")
    public ResponseEntity<List<PrescriptionDto>> getMyPrescriptions(Authentication authentication) {
        String email = authentication.getName();
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByPatientId(patient.get().getId());
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{prescriptionId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable UUID prescriptionId) {
        java.util.Optional<PrescriptionDto> prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return prescription.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get prescription details (drug items)
    @GetMapping("/{prescriptionId}/details")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<PrescriptionDetailDto>> getDetails(@PathVariable UUID prescriptionId) {
        List<PrescriptionDetailDto> details = prescriptionService.getDetails(prescriptionId);
        return ResponseEntity.ok(details);
    }

    // Finalize prescription (DRAFT -> FINAL)
    @PutMapping("/{prescriptionId}/finalize")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> finalizePrescription(@PathVariable UUID prescriptionId) {
        boolean finalized = prescriptionService.finalizePrescription(prescriptionId);
        return finalized ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Dispense medication (FINAL -> DISPENSED)
    @PutMapping("/{prescriptionId}/dispense")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> dispenseMedication(@PathVariable UUID prescriptionId) {
        boolean dispensed = prescriptionService.dispenseMedication(prescriptionId);
        return dispensed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // Calculate prescription total
    @GetMapping("/{prescriptionId}/total")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<BigDecimal> calculateTotal(@PathVariable UUID prescriptionId) {
        BigDecimal total = prescriptionService.calculateTotal(prescriptionId);
        return ResponseEntity.ok(total);
    }

    // Print prescription
    @GetMapping("/{prescriptionId}/print")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<String> printPrescription(@PathVariable UUID prescriptionId) {
        String printout = prescriptionService.printPrescription(prescriptionId);
        return ResponseEntity.ok(printout);
    }

    // Check allergies before prescribing
    @PostMapping("/check-allergies")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<AllergyCheckResponse> checkAllergies(
            @Valid @RequestBody AllergyCheckRequest request) {
        try {
            List<AllergyWarning> warnings = allergyCheckService.checkPatientAllergies(
                    request.getPatientId(),
                    request.getDrugIds());

            AllergyCheckResponse response = AllergyCheckResponse.builder()
                    .hasWarnings(!warnings.isEmpty())
                    .warnings(warnings)
                    .message(warnings.isEmpty()
                            ? "No allergy conflicts detected"
                            : "Allergy warnings found - review before prescribing")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff', 'admin')")
    public ResponseEntity<List<PrescriptionWithDetailsDto>> getPrescriptionsByDoctorId(@PathVariable UUID doctorId) {
        try {
            List<PrescriptionWithDetailsDto> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all-with-details")
    @PreAuthorize("hasAnyAuthority('staff', 'admin')")
    public ResponseEntity<List<PrescriptionWithDetailsDto>> getAllPrescriptionsWithDetails() {
        try {
            List<PrescriptionWithDetailsDto> prescriptions = prescriptionService.getAllPrescriptionsWithDetails();
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
