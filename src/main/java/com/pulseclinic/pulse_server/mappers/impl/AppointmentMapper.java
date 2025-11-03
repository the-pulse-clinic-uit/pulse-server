package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper implements Mapper<Appointment, AppointmentDto> {
    private final ModelMapper modelMapper;

    public AppointmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AppointmentDto mapTo(Appointment appointment) {
        return this.modelMapper.map(appointment, AppointmentDto.class);
    }

    @Override
    public Appointment mapFrom(AppointmentDto appointmentDto) {
        return this.modelMapper.map(appointmentDto, Appointment.class);
    }

    public Appointment mapFrom(AppointmentRequestDto appointmentRequestDto) {
        return this.modelMapper.map(appointmentRequestDto, Appointment.class);
    }
}
