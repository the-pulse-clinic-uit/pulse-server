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
    private final UserMapper userMapper;

    public PatientMapper(ModelMapper modelMapper, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PatientDto mapTo(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .healthInsuranceId(patient.getHealthInsuranceId())
                .bloodType(patient.getBloodType())
                .allergies(patient.getAllergies())
                .createdAt(patient.getCreatedAt())
                .userDto(patient.getUser() != null ? userMapper.mapTo(patient.getUser()) : null)
                .build();
    }

    @Override
    public Patient mapFrom(PatientDto patientDto) {
        return this.modelMapper.map(patientDto, Patient.class);
    }

    public Patient mapFrom(PatientRequestDto patientRequestDto) {
        return this.modelMapper.map(patientRequestDto, Patient.class);
    }
}
