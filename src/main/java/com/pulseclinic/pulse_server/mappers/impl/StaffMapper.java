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
    private final DepartmentMapper departmentMapper;

    public StaffMapper(ModelMapper modelMapper, UserMapper userMapper, DepartmentMapper departmentMapper){
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public StaffDto mapTo(Staff staff) {
        return StaffDto.builder()
                .id(staff.getId())
                .position(staff.getPosition())
                .createdAt(staff.getCreatedAt())
                .userDto(staff.getUser() != null ? userMapper.mapTo(staff.getUser()) : null)
                .departmentDto(staff.getDepartment() != null ? departmentMapper.mapTo(staff.getDepartment()) : null)
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
