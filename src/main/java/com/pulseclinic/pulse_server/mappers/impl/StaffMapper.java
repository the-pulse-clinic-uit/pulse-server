package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper implements Mapper<Staff, StaffDto> {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public StaffMapper(ModelMapper modelMapper, UserMapper userMapper){
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

    @Override
    public StaffDto mapTo(Staff staff) {
        return StaffDto.builder()
                .id(staff.getId())
                .position(staff.getPosition())
                .createdAt(staff.getCreatedAt())
                .userDto(staff.getUser() != null ? userMapper.mapTo(staff.getUser()) : null)
                .build();
    }

    @Override
    public Staff mapFrom(StaffDto staffDto) {
        return this.modelMapper.map(staffDto, Staff.class);
    }

    public Staff mapFrom(StaffRequestDto staffRequestDto) {
        return this.modelMapper.map(staffRequestDto, Staff.class);
    }
}
