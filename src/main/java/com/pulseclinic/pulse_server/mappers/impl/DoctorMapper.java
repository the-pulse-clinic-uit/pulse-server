package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper implements Mapper<Doctor, DoctorDto> {
    private final ModelMapper modelMapper;

    public DoctorMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public DoctorDto mapTo(Doctor doctor) {
        return this.modelMapper.map(doctor, DoctorDto.class);
    }

    @Override
    public Doctor mapFrom(DoctorDto doctorDto) {
        return this.modelMapper.map(doctorDto, Doctor.class);
    }

    public Doctor mapFrom(DoctorRequestDto doctorRequestDto) {
        return this.modelMapper.map(doctorRequestDto, Doctor.class);
    }
}
