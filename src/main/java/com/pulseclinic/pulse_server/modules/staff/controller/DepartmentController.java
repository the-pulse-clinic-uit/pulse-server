package com.pulseclinic.pulse_server.modules.staff.controller;

import com.pulseclinic.pulse_server.modules.staff.service.DepartmentService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepartmentController {

    private final DepartmentService departmentService;
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
}
