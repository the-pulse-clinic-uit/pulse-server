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

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final EncounterRepository encounterRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              EncounterRepository encounterRepository,
                              InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.encounterRepository = encounterRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    @Transactional
    public InvoiceDto createInvoice(UUID encounterId, InvoiceRequestDto invoiceRequestDto) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Encounter not found");
        }

        Optional<Invoice> existingInvoice = invoiceRepository.findByEncounterIdAndDeletedAtIsNull(encounterId);
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("Invoice already exists for this encounter");
        }

        Invoice invoice = Invoice.builder()
                .dueDate(invoiceRequestDto.getDueDate())
                .totalAmount(invoiceRequestDto.getTotalAmount())
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

    @Override
    @Transactional
    public boolean recordPayment(UUID invoiceId, BigDecimal amount) {
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
            if (invoiceOpt.isEmpty()) {
                return false;
            }

            Invoice invoice = invoiceOpt.get();
            BigDecimal currentPaid = invoice.getAmountPaid();
            BigDecimal newPaid = currentPaid.add(amount);

            if (newPaid.compareTo(invoice.getTotalAmount()) > 0) {
                return false;
            }

            invoice.setAmountPaid(newPaid);
            invoiceRepository.save(invoice);

            updateStatus(invoiceId);
            return true;
        } catch (Exception e) {
            return false;
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
        // Kiểm tra quá hạn nếu chưa thanh toán và đã qua ngày đến hạn
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
