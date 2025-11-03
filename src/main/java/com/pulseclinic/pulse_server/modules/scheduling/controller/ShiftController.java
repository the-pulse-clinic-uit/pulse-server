package com.pulseclinic.pulse_server.modules.scheduling.controller;

import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }
}
