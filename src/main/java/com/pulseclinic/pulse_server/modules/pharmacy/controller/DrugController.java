package com.pulseclinic.pulse_server.modules.pharmacy.controller;

import com.pulseclinic.pulse_server.modules.pharmacy.service.DrugService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrugController {
    private final DrugService drugService;

    public DrugController(DrugService drugService){
        this.drugService = drugService;
    }
}
