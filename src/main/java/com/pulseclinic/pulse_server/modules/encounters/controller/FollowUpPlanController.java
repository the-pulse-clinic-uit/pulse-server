package com.pulseclinic.pulse_server.modules.encounters.controller;

import com.pulseclinic.pulse_server.modules.encounters.service.FollowUpPlanService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowUpPlanController {
    private final FollowUpPlanService followUpPlanService;
    public FollowUpPlanController(FollowUpPlanService followUpPlanService) {
        this.followUpPlanService = followUpPlanService;
    }
}
