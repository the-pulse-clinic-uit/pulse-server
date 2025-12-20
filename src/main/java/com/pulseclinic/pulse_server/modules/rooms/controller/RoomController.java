package com.pulseclinic.pulse_server.modules.rooms.controller;

import com.pulseclinic.pulse_server.mappers.impl.RoomMapper;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomRequestDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Room room = this.roomService.createRoom(this.roomMapper.mapFrom(roomRequestDto));
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.CREATED);
    }

    @GetMapping("/by-department")
    public ResponseEntity<List<RoomDto>> getAll(@RequestParam UUID departmentId) {
        List<Room> rooms = this.roomService.findAllByDepartmentId(departmentId);
        return new ResponseEntity<>(rooms.stream().map(r->this.roomMapper.mapTo(r)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable UUID id) {
        Room room = this.roomService.findById(id);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> rooms = this.roomService.findAll();
        return new ResponseEntity<>(rooms.stream().map(r->this.roomMapper.mapTo(r)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<RoomDto> updateStatus(@PathVariable UUID id) {
        Room room = this.roomService.updateStatus(id);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable UUID id, @RequestBody RoomDto roomDto) {
        Room room = this.roomService.updateRoom(id, roomDto);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        this.roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
