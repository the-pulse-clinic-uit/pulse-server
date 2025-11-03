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

    public RoomMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RoomDto mapTo(Room room) {
        return this.modelMapper.map(room, RoomDto.class);
    }

    @Override
    public Room mapFrom(RoomDto roomDto) {
        return this.modelMapper.map(roomDto, Room.class);
    }

    public Room mapFrom(RoomRequestDto roomRequestDto) {
        return this.modelMapper.map(roomRequestDto, Room.class);
    }
}
