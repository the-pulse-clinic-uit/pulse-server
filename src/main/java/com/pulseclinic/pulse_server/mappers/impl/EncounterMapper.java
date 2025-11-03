package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EncounterMapper implements Mapper<Encounter, EncounterDto> {
    private final ModelMapper modelMapper;

    public EncounterMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public EncounterDto mapTo(Encounter encounter) {
        return this.modelMapper.map(encounter, EncounterDto.class);
    }

    @Override
    public Encounter mapFrom(EncounterDto encounterDto) {
        return this.modelMapper.map(encounterDto, Encounter.class);
    }

    public Encounter mapFrom(EncounterRequestDto encounterRequestDto) {
        return this.modelMapper.map(encounterRequestDto, Encounter.class);
    }
}
