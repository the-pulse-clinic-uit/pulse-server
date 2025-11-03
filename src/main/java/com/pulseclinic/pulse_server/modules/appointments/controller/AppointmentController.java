package com.pulseclinic.pulse_server.modules.appointments.controller;

import com.pulseclinic.pulse_server.modules.appointments.service.AppointmentService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppointmentController {
    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}
