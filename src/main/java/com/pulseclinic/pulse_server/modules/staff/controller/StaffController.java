package com.pulseclinic.pulse_server.modules.staff.controller;

import com.pulseclinic.pulse_server.modules.staff.service.StaffService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }
}
