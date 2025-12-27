package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper implements Mapper<Department, DepartmentDto> {
    private final ModelMapper modelMapper;

    public DepartmentMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public DepartmentDto mapTo(Department department) {
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .build();
    }

    @Override
    public Department mapFrom(DepartmentDto departmentDto) {
        return this.modelMapper.map(departmentDto, Department.class);
    }

    public Department mapFrom(DepartmentRequestDto departmentRequestDto) {
        return this.modelMapper.map(departmentRequestDto, Department.class);
    }
}
