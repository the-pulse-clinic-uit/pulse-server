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

    public StaffMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public StaffDto mapTo(Staff staff) {
        return this.modelMapper.map(staff, StaffDto.class);
    }

    @Override
    public Staff mapFrom(StaffDto staffDto) {
        return this.modelMapper.map(staffDto, Staff.class);
    }

    public Staff mapFrom(StaffRequestDto staffRequestDto) {
        return this.modelMapper.map(staffRequestDto, Staff.class);
    }
}
