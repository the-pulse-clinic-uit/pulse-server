package com.pulseclinic.pulse_server.modules.users.controller;

import com.pulseclinic.pulse_server.modules.users.service.RoleService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
}
