package com.pulseclinic.pulse_server.modules.staff.service.impl;

import com.pulseclinic.pulse_server.modules.staff.service.StaffService;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffService staffService;
    public StaffServiceImpl(StaffService staffService) {
        this.staffService = staffService;
    }
}
