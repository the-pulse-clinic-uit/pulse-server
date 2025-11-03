package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.WaitlistEntry;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WaitlistEntryMapper implements Mapper<WaitlistEntry, WaitlistEntryDto> {
    private final ModelMapper modelMapper;

    public WaitlistEntryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public WaitlistEntryDto mapTo(WaitlistEntry waitlistEntry) {
        return this.modelMapper.map(waitlistEntry, WaitlistEntryDto.class);
    }

    @Override
    public WaitlistEntry mapFrom(WaitlistEntryDto waitlistEntryDto) {
        return this.modelMapper.map(waitlistEntryDto, WaitlistEntry.class);
    }

    public WaitlistEntry mapFrom(WaitlistEntryRequestDto waitlistEntryRequestDto) {
        return this.modelMapper.map(waitlistEntryRequestDto, WaitlistEntry.class);
    }
}
