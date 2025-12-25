package com.pulseclinic.pulse_server.modules.rooms.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.mappers.impl.RoomMapper;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomRequestDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.service.RoomService;

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
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Room room = this.roomService.createRoom(this.roomMapper.mapFrom(roomRequestDto));
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.CREATED);
    }

    @GetMapping("/by-department")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<List<RoomDto>> getAll(@RequestParam UUID departmentId) {
        List<Room> rooms = this.roomService.findAllByDepartmentId(departmentId);
        return new ResponseEntity<>(rooms.stream().map(r->this.roomMapper.mapTo(r)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<RoomDto> getRoom(@PathVariable UUID id) {
        Room room = this.roomService.findById(id);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> rooms = this.roomService.findAll();
        return new ResponseEntity<>(rooms.stream().map(r->this.roomMapper.mapTo(r)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<RoomDto> updateStatus(@PathVariable UUID id) {
        Room room = this.roomService.updateStatus(id);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable UUID id, @RequestBody RoomDto roomDto) {
        Room room = this.roomService.updateRoom(id, roomDto);
        return new ResponseEntity<>(this.roomMapper.mapTo(room),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'staff')")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        this.roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
