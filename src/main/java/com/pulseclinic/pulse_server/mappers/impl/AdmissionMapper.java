package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AdmissionMapper implements Mapper<Admission, AdmissionDto> {
    private final ModelMapper modelMapper;

    public AdmissionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AdmissionDto mapTo(Admission admission) {
        return this.modelMapper.map(admission, AdmissionDto.class);
    }

    @Override
    public Admission mapFrom(AdmissionDto admissionDto) {
        return this.modelMapper.map(admissionDto, Admission.class);
    }

    public Admission mapFrom(AdmissionRequestDto admissionRequestDto) {
        return this.modelMapper.map(admissionRequestDto, Admission.class);
    }
}
