package com.pulseclinic.pulse_server.modules.ratings.controller;

import com.pulseclinic.pulse_server.modules.ratings.service.StaffRatingService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaffRatingController {
    private final StaffRatingService staffRatingService;

    public StaffRatingController(StaffRatingService staffRatingService) {
        this.staffRatingService = staffRatingService;
    }
}
