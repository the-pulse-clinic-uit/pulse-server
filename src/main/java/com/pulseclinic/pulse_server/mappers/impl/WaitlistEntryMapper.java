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
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;

    public WaitlistEntryMapper(ModelMapper modelMapper, PatientMapper patientMapper, DoctorMapper doctorMapper) {
        this.modelMapper = modelMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public WaitlistEntryDto mapTo(WaitlistEntry waitlistEntry) {
        return WaitlistEntryDto.builder()
                .id(waitlistEntry.getId())
                .dutyDate(waitlistEntry.getDutyDate())
                .ticketNo(waitlistEntry.getTicketNo())
                .notes(waitlistEntry.getNotes())
                .priority(waitlistEntry.getPriority())
                .status(waitlistEntry.getStatus())
                .createdAt(waitlistEntry.getCreatedAt())
                .calledAt(waitlistEntry.getCalledAt())
                .servedAt(waitlistEntry.getServedAt())
                .patientDto(waitlistEntry.getPatient() != null ? patientMapper.mapTo(waitlistEntry.getPatient()) : null)
                .doctorDto(waitlistEntry.getDoctor() != null ? doctorMapper.mapTo(waitlistEntry.getDoctor()) : null)
                // Use ModelMapper for appointment to avoid circular dependency
                .appointmentDto(waitlistEntry.getAppointment() != null ? modelMapper.map(waitlistEntry.getAppointment(), com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto.class) : null)
                .build();
    }

    @Override
    public WaitlistEntry mapFrom(WaitlistEntryDto waitlistEntryDto) {
        return this.modelMapper.map(waitlistEntryDto, WaitlistEntry.class);
    }

    public WaitlistEntry mapFrom(WaitlistEntryRequestDto waitlistEntryRequestDto) {
        return this.modelMapper.map(waitlistEntryRequestDto, WaitlistEntry.class);
    }
}
