package com.pulseclinic.pulse_server.modules.encounters.controller;

import com.pulseclinic.pulse_server.modules.encounters.service.EncounterService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EncounterController {
    private final EncounterService encounterService;
    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
