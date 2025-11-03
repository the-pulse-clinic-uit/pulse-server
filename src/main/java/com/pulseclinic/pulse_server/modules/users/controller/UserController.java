package com.pulseclinic.pulse_server.modules.users.controller;

import com.pulseclinic.pulse_server.modules.users.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
