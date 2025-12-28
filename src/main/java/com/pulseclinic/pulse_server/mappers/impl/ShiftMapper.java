package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class ShiftMapper implements Mapper<Shift, ShiftDto> {
    private final ModelMapper modelMapper;
    private final DepartmentMapper departmentMapper;
    private final RoomMapper roomMapper;

    public ShiftMapper(ModelMapper modelMapper, DepartmentMapper departmentMapper, RoomMapper roomMapper) {
        this.modelMapper = modelMapper;
        this.departmentMapper = departmentMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    public ShiftDto mapTo(Shift shift) {
        return ShiftDto.builder()
                .id(shift.getId())
                .name(shift.getName())
                .kind(shift.getKind())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .slotMinutes(shift.getSlotMinutes())
                .capacityPerSlot(shift.getCapacityPerSlot())
                .createdAt(shift.getCreatedAt())
                .departmentDto(shift.getDepartment() != null ? departmentMapper.mapTo(shift.getDepartment()) : null)
                .defaultRoomDto(shift.getDefaultRoom() != null ? roomMapper.mapTo(shift.getDefaultRoom()) : null)
                .build();
    }

    @Override
    public Shift mapFrom(ShiftDto shiftDto) {
        return this.modelMapper.map(shiftDto, Shift.class);
    }

    public Shift mapFrom(ShiftRequestDto shiftRequestDto) {
        return this.modelMapper.map(shiftRequestDto, Shift.class);
    }
}
