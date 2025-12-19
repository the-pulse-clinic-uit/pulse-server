package com.pulseclinic.pulse_server.modules.rooms.service;

import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    Room createRoom(Room room);
    List<Room> findAll();
    List<Room> findAllByDepartmentId(UUID departmentId);
    Room findById(UUID id);
    Room updateStatus(UUID id);
    void deleteRoom(UUID id);
    Room updateRoom(UUID id, RoomDto roomDto);
}
