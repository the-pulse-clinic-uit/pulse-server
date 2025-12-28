package com.pulseclinic.pulse_server.modules.billing.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceDto;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceRequestDto;
import com.pulseclinic.pulse_server.modules.billing.service.InvoiceService;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final PatientRepository patientRepository;

    public InvoiceController(InvoiceService invoiceService, PatientRepository patientRepository) {
        this.invoiceService = invoiceService;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staffs')")
    public ResponseEntity<InvoiceDto> createInvoice(
            @Valid @RequestBody InvoiceRequestDto invoiceRequestDto) {
        try {
            InvoiceDto invoice = invoiceService.createInvoice(invoiceRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable UUID invoiceId) {
        return invoiceService.getInvoiceById(invoiceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff', 'patient')")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByPatientId(@PathVariable UUID patientId) {
        List<InvoiceDto> invoices = invoiceService.getInvoicesByPatientId(patientId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('patient')")
    public ResponseEntity<List<InvoiceDto>> getMyInvoices(Authentication authentication) {
        String email = authentication.getName();
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<InvoiceDto> invoices = invoiceService.getInvoicesByPatientId(patient.get().getId());
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{invoiceId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID invoiceId) {
        BigDecimal balance = invoiceService.getBalance(invoiceId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{invoiceId}/line_items")
    public ResponseEntity<List<Map<String, Object>>> getLineItems(@PathVariable UUID invoiceId) {
        List<Map<String, Object>> lineItems = invoiceService.getLineItems(invoiceId);
        return ResponseEntity.ok(lineItems);
    }

    @PostMapping("/{invoiceId}/line_item")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> addLineItem(
            @PathVariable UUID invoiceId,
            @RequestParam String description,
            @RequestParam BigDecimal amount) {
        boolean added = invoiceService.addLineItem(invoiceId, description, amount);
        return added ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{invoiceId}/discount")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> applyDiscount(
            @PathVariable UUID invoiceId,
            @RequestParam BigDecimal discount) {
        boolean applied = invoiceService.applyDiscount(invoiceId, discount);
        return applied ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{invoiceId}/void")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<Void> voidInvoice(
            @PathVariable UUID invoiceId,
            @RequestParam(required = false) String reason) {
        boolean voided = invoiceService.voidInvoice(invoiceId, reason);
        return voided ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{invoiceId}/create-payment")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<String> createPayment(
            @PathVariable UUID invoiceId
    ) {
        String result = invoiceService.createPayment(invoiceId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{invoiceId}/record-payment")
    public ResponseEntity<Void> recordPayment(
            @PathVariable UUID invoiceId,
            @RequestParam BigDecimal amount) {
        boolean recorded = invoiceService.recordPayment(invoiceId, amount);
        return recorded ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
