package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionDetailService;
import com.pulseclinic.pulse_server.modules.pharmacy.service.PrescriptionService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrescriptionDetailController {
    private final PrescriptionDetailService prescriptionDetailService;

    public PrescriptionDetailController(PrescriptionDetailService prescriptionDetailService) {
        this.prescriptionDetailService = prescriptionDetailService;
    }
}
