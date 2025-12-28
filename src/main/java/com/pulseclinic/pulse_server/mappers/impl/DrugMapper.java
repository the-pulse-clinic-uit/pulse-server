package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DrugMapper implements Mapper<Drug, DrugDto> {
    private final ModelMapper modelMapper;

    public DrugMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DrugDto mapTo(Drug drug) {
        return DrugDto.builder()
                .id(drug.getId())
                .name(drug.getName())
                .dosageForm(drug.getDosageForm())
                .unit(drug.getUnit())
                .strength(drug.getStrength())
                .createdAt(drug.getCreatedAt())
                .unitPrice(drug.getUnitPrice())
                .build();
    }

    @Override
    public Drug mapFrom(DrugDto drugDto) {
        return this.modelMapper.map(drugDto, Drug.class);
    }

    public Drug mapFrom(DrugRequestDto drugRequestDto) {
        return this.modelMapper.map(drugRequestDto, Drug.class);
    }
}
