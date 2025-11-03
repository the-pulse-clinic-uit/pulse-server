package com.pulseclinic.pulse_server.modules.billing.controller;

import com.pulseclinic.pulse_server.modules.billing.service.InvoiceService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
}
