package com.pulseclinic.pulse_server.modules.billing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceDto;
import com.pulseclinic.pulse_server.modules.billing.dto.InvoiceRequestDto;

public interface InvoiceService {
    InvoiceDto createInvoice(UUID encounterId, InvoiceRequestDto invoiceRequestDto);
    Optional<InvoiceDto> getInvoiceById(UUID invoiceId);
    BigDecimal getBalance(UUID invoiceId);
    List<Map<String, Object>> getLineItems(UUID invoiceId);
    boolean addLineItem(UUID invoiceId, String description, BigDecimal amount);
    boolean applyDiscount(UUID invoiceId, BigDecimal discount);
    boolean voidInvoice(UUID invoiceId, String reason);
    String createPayment(BigDecimal amount);
    boolean recordPayment(UUID invoiceId, BigDecimal amount);
}
