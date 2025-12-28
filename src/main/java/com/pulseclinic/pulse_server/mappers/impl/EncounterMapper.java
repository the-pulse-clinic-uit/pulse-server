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
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;

    public EncounterMapper(ModelMapper modelMapper, PatientMapper patientMapper, DoctorMapper doctorMapper) {
        this.modelMapper = modelMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public EncounterDto mapTo(Encounter encounter) {
        return EncounterDto.builder()
                .id(encounter.getId())
                .type(encounter.getType())
                .startedAt(encounter.getStartedAt())
                .endedAt(encounter.getEndedAt())
                .diagnosis(encounter.getDiagnosis())
                .notes(encounter.getNotes())
                .createdAt(encounter.getCreatedAt())
                .patientDto(encounter.getPatient() != null ? patientMapper.mapTo(encounter.getPatient()) : null)
                .doctorDto(encounter.getDoctor() != null ? doctorMapper.mapTo(encounter.getDoctor()) : null)
                // Use ModelMapper for appointment to avoid circular dependency
                .appointmentDto(encounter.getAppointment() != null ? modelMapper.map(encounter.getAppointment(), com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto.class) : null)
                .build();
    }

    @Override
    public Encounter mapFrom(EncounterDto encounterDto) {
        return this.modelMapper.map(encounterDto, Encounter.class);
    }

    public Encounter mapFrom(EncounterRequestDto encounterRequestDto) {
        return this.modelMapper.map(encounterRequestDto, Encounter.class);
    }
}
