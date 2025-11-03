package com.pulseclinic.pulse_server.modules.admissions.controller;

import com.pulseclinic.pulse_server.modules.admissions.service.AdmissionService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdmissionController {
    private AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }
}
