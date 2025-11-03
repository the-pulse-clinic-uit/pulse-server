package com.pulseclinic.pulse_server.modules.rooms.controller;

import com.pulseclinic.pulse_server.modules.rooms.service.RoomService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
}
