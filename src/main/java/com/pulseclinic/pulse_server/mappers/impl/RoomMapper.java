package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomRequestDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper implements Mapper<Room, RoomDto> {
    private final ModelMapper modelMapper;
    private final DepartmentMapper departmentMapper;

    public RoomMapper(ModelMapper modelMapper, DepartmentMapper departmentMapper) {
        this.modelMapper = modelMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public RoomDto mapTo(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .bedAmount(room.getBedAmount())
                .isAvailable(room.getIsAvailable())
                .createdAt(room.getCreatedAt())
                .departmentDto(room.getDepartment() != null ? departmentMapper.mapTo(room.getDepartment()) : null)
                .build();
    }

    @Override
    public Room mapFrom(RoomDto roomDto) {
        return this.modelMapper.map(roomDto, Room.class);
    }

    public Room mapFrom(RoomRequestDto roomRequestDto) {
        return this.modelMapper.map(roomRequestDto, Room.class);
    }
}
