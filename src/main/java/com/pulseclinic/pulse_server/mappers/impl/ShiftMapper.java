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

    public ShiftMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ShiftDto mapTo(Shift shift) {
        return this.modelMapper.map(shift, ShiftDto.class);
    }

    @Override
    public Shift mapFrom(ShiftDto shiftDto) {
        return this.modelMapper.map(shiftDto, Shift.class);
    }

    public Shift mapFrom(ShiftRequestDto shiftRequestDto) {
        return this.modelMapper.map(shiftRequestDto, Shift.class);
    }
}
