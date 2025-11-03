package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class ShiftAssignmentMapper implements Mapper<ShiftAssignment, ShiftAssignmentDto> {
    private final ModelMapper modelMapper;

    public ShiftAssignmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ShiftAssignmentDto mapTo(ShiftAssignment shiftAssignment) {
        return this.modelMapper.map(shiftAssignment, ShiftAssignmentDto.class);
    }

    @Override
    public ShiftAssignment mapFrom(ShiftAssignmentDto shiftAssignmentDto) {
        return this.modelMapper.map(shiftAssignmentDto, ShiftAssignment.class);
    }

    public ShiftAssignment mapFrom(ShiftAssignmentRequestDto shiftAssignmentRequestDto) {
        return this.modelMapper.map(shiftAssignmentRequestDto, ShiftAssignment.class);
    }
}
