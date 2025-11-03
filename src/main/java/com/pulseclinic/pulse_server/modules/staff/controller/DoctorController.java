package com.pulseclinic.pulse_server.modules.staff.controller;

import com.pulseclinic.pulse_server.modules.staff.service.DoctorService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
}
