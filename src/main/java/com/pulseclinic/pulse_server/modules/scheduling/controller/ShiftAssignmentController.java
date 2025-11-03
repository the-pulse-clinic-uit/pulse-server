package com.pulseclinic.pulse_server.modules.scheduling.controller;

import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftAssignmentService;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShiftAssignmentController {
    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
    }
}
