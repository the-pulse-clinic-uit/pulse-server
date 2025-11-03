package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }
}
