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
    private final DoctorMapper doctorMapper;
    private final ShiftMapper shiftMapper;
    private final RoomMapper roomMapper;

    public ShiftAssignmentMapper(ModelMapper modelMapper, DoctorMapper doctorMapper,
                                ShiftMapper shiftMapper, RoomMapper roomMapper) {
        this.modelMapper = modelMapper;
        this.doctorMapper = doctorMapper;
        this.shiftMapper = shiftMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    public ShiftAssignmentDto mapTo(ShiftAssignment shiftAssignment) {
        return ShiftAssignmentDto.builder()
                .id(shiftAssignment.getId())
                .dutyDate(shiftAssignment.getDutyDate())
                .roleInShift(shiftAssignment.getRoleInShift())
                .status(shiftAssignment.getStatus())
                .notes(shiftAssignment.getNotes())
                .createdAt(shiftAssignment.getCreatedAt())
                .updatedAt(shiftAssignment.getUpdatedAt())
                .doctorDto(shiftAssignment.getDoctor() != null ? doctorMapper.mapTo(shiftAssignment.getDoctor()) : null)
                .shiftDto(shiftAssignment.getShift() != null ? shiftMapper.mapTo(shiftAssignment.getShift()) : null)
                .roomDto(shiftAssignment.getRoom() != null ? roomMapper.mapTo(shiftAssignment.getRoom()) : null)
                .build();
    }

    @Override
    public ShiftAssignment mapFrom(ShiftAssignmentDto shiftAssignmentDto) {
        return this.modelMapper.map(shiftAssignmentDto, ShiftAssignment.class);
    }

    public ShiftAssignment mapFrom(ShiftAssignmentRequestDto shiftAssignmentRequestDto) {
        return this.modelMapper.map(shiftAssignmentRequestDto, ShiftAssignment.class);
    }
}
