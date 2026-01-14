package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterSummaryDto;
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
                .rating(encounter.getRating())
                .ratingComment(encounter.getRatingComment())
                .ratedAt(encounter.getRatedAt())
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

    public EncounterSummaryDto mapToSummary(Encounter encounter) {
        String patientName = null;
        if (encounter.getPatient() != null && encounter.getPatient().getUser() != null) {
            patientName = encounter.getPatient().getUser().getFullName();
        }

        String doctorName = null;
        if (encounter.getDoctor() != null && encounter.getDoctor().getStaff() != null
                && encounter.getDoctor().getStaff().getUser() != null) {
            doctorName = encounter.getDoctor().getStaff().getUser().getFullName();
        }

        return EncounterSummaryDto.builder()
                .id(encounter.getId())
                .type(encounter.getType())
                .startedAt(encounter.getStartedAt())
                .endedAt(encounter.getEndedAt())
                .diagnosis(encounter.getDiagnosis())
                .notes(encounter.getNotes())
                .createdAt(encounter.getCreatedAt())
                .patientId(encounter.getPatient() != null ? encounter.getPatient().getId() : null)
                .patientName(patientName)
                .doctorId(encounter.getDoctor() != null ? encounter.getDoctor().getId() : null)
                .doctorName(doctorName)
                .appointmentId(encounter.getAppointment() != null ? encounter.getAppointment().getId() : null)
                .build();
    }
}
