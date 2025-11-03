package com.pulseclinic.pulse_server.modules.scheduling.controller;

import com.pulseclinic.pulse_server.modules.scheduling.service.WaitlistEntryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WaitlistEntryController {
    private final WaitlistEntryService waitlistEntryService;

    public WaitlistEntryController(WaitlistEntryService waitlistEntryService) {
        this.waitlistEntryService = waitlistEntryService;
    }
}
