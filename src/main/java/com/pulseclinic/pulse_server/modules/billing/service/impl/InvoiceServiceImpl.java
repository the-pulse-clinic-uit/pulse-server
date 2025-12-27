package com.pulseclinic.pulse_server.modules.billing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.mappers.impl.InvoiceMapper;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceDto;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceRequestDto;
import com.pulseclinic.pulse_server.modules.billing.entity.Invoice;
import com.pulseclinic.pulse_server.modules.billing.repository.InvoiceRepository;
import com.pulseclinic.pulse_server.modules.billing.service.InvoiceService;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final EncounterRepository encounterRepository;
    private final InvoiceMapper invoiceMapper;
    private final WebClient webClient;
    private final PrescriptionRepository prescriptionRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              EncounterRepository encounterRepository,
                              InvoiceMapper invoiceMapper,
                              WebClient webClient,
                              PrescriptionRepository prescriptionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.encounterRepository = encounterRepository;
        this.invoiceMapper = invoiceMapper;
        this.webClient = webClient;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    @Transactional
    public InvoiceDto createInvoice(InvoiceRequestDto invoiceRequestDto) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(invoiceRequestDto.getEncounterId());
        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Encounter not found");
        }

        Optional<Invoice> existingInvoice = invoiceRepository.findByEncounterIdAndDeletedAtIsNull(invoiceRequestDto.getEncounterId());
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("Invoice already exists for this encounter");
        }

        // Set default due date to 30 days from now if not provided
        LocalDate dueDate = invoiceRequestDto.getDueDate() != null
            ? invoiceRequestDto.getDueDate()
            : LocalDate.now().plusDays(30);

        // Calculate total from all prescriptions linked to this encounter
        List<Prescription> prescriptions = prescriptionRepository.findByEncounterIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            invoiceRequestDto.getEncounterId()
        );

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Prescription prescription : prescriptions) {
            // Add prescription total price (which is already calculated when drugs are added)
            if (prescription.getTotalPrice() != null) {
                totalAmount = totalAmount.add(prescription.getTotalPrice());
            }
        }

        Invoice invoice = Invoice.builder()
                .dueDate(dueDate)
                .amountPaid(invoiceRequestDto.getAmountPaid() != null ? invoiceRequestDto.getAmountPaid() : BigDecimal.ZERO)
                .totalAmount(totalAmount) // Auto-calculated from prescriptions
                .status(invoiceRequestDto.getStatus() != null ? invoiceRequestDto.getStatus() : InvoiceStatus.UNPAID)
                .encounter(encounterOpt.get())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.mapTo(savedInvoice);
    }

    @Override
    public Optional<InvoiceDto> getInvoiceById(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        return invoiceOpt.map(invoiceMapper::mapTo);
    }

    @Override
    public BigDecimal getBalance(UUID invoiceId) {
        return calculateBalance(invoiceId);
    }

    @Override
    public List<Map<String, Object>> getLineItems(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Invoice invoice = invoiceOpt.get();
        List<Map<String, Object>> lineItems = new ArrayList<>();
        
        // Base invoice line item
        Map<String, Object> baseItem = new HashMap<>();
        baseItem.put("description", "Medical Services");
        baseItem.put("amount", invoice.getTotalAmount());
        lineItems.add(baseItem);
        
        return lineItems;
    }

    @Override
    @Transactional
    public boolean addLineItem(UUID invoiceId, String description, BigDecimal amount) {
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
            if (invoiceOpt.isEmpty()) {
                return false;
            }

            Invoice invoice = invoiceOpt.get();
            BigDecimal currentTotal = invoice.getTotalAmount();
            invoice.setTotalAmount(currentTotal.add(amount));
            invoiceRepository.save(invoice);

            updateStatus(invoiceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean applyDiscount(UUID invoiceId, BigDecimal discount) {
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
            if (invoiceOpt.isEmpty()) {
                return false;
            }

            Invoice invoice = invoiceOpt.get();
            BigDecimal currentTotal = invoice.getTotalAmount();
            BigDecimal newTotal = currentTotal.subtract(discount);

            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            invoice.setTotalAmount(newTotal);
            invoiceRepository.save(invoice);

            updateStatus(invoiceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean voidInvoice(UUID invoiceId, String reason) {
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
            if (invoiceOpt.isEmpty()) {
                return false;
            }

            Invoice invoice = invoiceOpt.get();
            invoice.setStatus(InvoiceStatus.VOID);
            invoiceRepository.save(invoice);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    @Override
    public String createPayment(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            throw new RuntimeException("Invoice not found");
        }

        Invoice invoice = invoiceOpt.get();
        String amount = invoice.getTotalAmount().toString().split("\\.")[0];


        return webClient.get()
                .uri("http://localhost:8081/api/v1/payment/vn-pay?amount=" + amount)
                .header("X-INVOICE-ID", String.valueOf(invoiceId))
                .retrieve()
                .bodyToMono(String.class)
                .block(); // giống await fetch()
    }

    @Override
    @Transactional
    public boolean recordPayment(UUID invoiceId, BigDecimal amount) {
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
            if (invoiceOpt.isEmpty()) {
                return false;
            }

            amount = amount.divide(new BigDecimal(100));

            Invoice invoice = invoiceOpt.get();
            BigDecimal currentPaid = invoice.getAmountPaid();
            BigDecimal newPaid = currentPaid.add(amount);

            if (newPaid.compareTo(invoice.getTotalAmount()) > 0) {
                return false;
            }

            invoice.setAmountPaid(newPaid);
            invoiceRepository.save(invoice);

            updateStatus(invoiceId);

            // Auto-dispense prescriptions when invoice is fully paid
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                autoDispensePrescriptions(invoice);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void autoDispensePrescriptions(Invoice invoice) {
        try {
            // Find all prescriptions for this encounter
            List<Prescription> prescriptions = prescriptionRepository
                .findByEncounterIdAndDeletedAtIsNullOrderByCreatedAtDesc(invoice.getEncounter().getId());

            for (Prescription prescription : prescriptions) {
                // Only dispense if prescription is in FINAL status
                if (prescription.getStatus() == com.pulseclinic.pulse_server.enums.PrescriptionStatus.FINAL) {
                    prescription.setStatus(com.pulseclinic.pulse_server.enums.PrescriptionStatus.DISPENSED);
                    prescriptionRepository.save(prescription);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the payment
            System.err.println("Error auto-dispensing prescriptions: " + e.getMessage());
        }
    }

    private void updateStatus(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return;
        }

        Invoice invoice = invoiceOpt.get();
        BigDecimal balance = calculateBalance(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.VOID) {
            return;
        }

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (balance.compareTo(invoice.getTotalAmount()) < 0) {
            invoice.setStatus(InvoiceStatus.PARTIAL);
        } else if (isOverdue(invoiceId)) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        } else {
            invoice.setStatus(InvoiceStatus.UNPAID);
        }

        invoiceRepository.save(invoice);
    }

    private boolean isOverdue(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return false;
        }

        Invoice invoice = invoiceOpt.get();
        if (invoice.getStatus() != InvoiceStatus.PAID &&
                invoice.getStatus() != InvoiceStatus.VOID) {
            return invoice.getDueDate().isBefore(LocalDate.now());
        }
        return false;
    }


    private BigDecimal calculateBalance(UUID invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Invoice invoice = invoiceOpt.get();
        return invoice.getTotalAmount().subtract(invoice.getAmountPaid());
    }
}
