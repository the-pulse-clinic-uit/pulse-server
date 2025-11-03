package com.pulseclinic.pulse_server.modules.patients.controller;

import com.pulseclinic.pulse_server.modules.patients.service.PatientService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
}
