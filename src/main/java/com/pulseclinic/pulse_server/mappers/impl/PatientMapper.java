package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper implements Mapper<Patient, PatientDto> {
    private final ModelMapper modelMapper;

    public PatientMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PatientDto mapTo(Patient patient) {
        return this.modelMapper.map(patient, PatientDto.class);
    }

    @Override
    public Patient mapFrom(PatientDto patientDto) {
        return this.modelMapper.map(patientDto, Patient.class);
    }

    public Patient mapFrom(PatientRequestDto patientRequestDto) {
        return this.modelMapper.map(patientRequestDto, Patient.class);
    }
}
