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
    private final EncounterMapper encounterMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final RoomMapper roomMapper;

    public AdmissionMapper(ModelMapper modelMapper, EncounterMapper encounterMapper,
                          PatientMapper patientMapper, DoctorMapper doctorMapper, RoomMapper roomMapper) {
        this.modelMapper = modelMapper;
        this.encounterMapper = encounterMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    public AdmissionDto mapTo(Admission admission) {
        return AdmissionDto.builder()
                .id(admission.getId())
                .status(admission.getStatus())
                .notes(admission.getNotes())
                .admittedAt(admission.getAdmittedAt())
                .dischargedAt(admission.getDischargedAt())
                .encounterDto(admission.getEncounter() != null ? encounterMapper.mapTo(admission.getEncounter()) : null)
                .patientDto(admission.getPatient() != null ? patientMapper.mapTo(admission.getPatient()) : null)
                .doctorDto(admission.getDoctor() != null ? doctorMapper.mapTo(admission.getDoctor()) : null)
                .roomDto(admission.getRoom() != null ? roomMapper.mapTo(admission.getRoom()) : null)
                .build();
    }

    @Override
    public Admission mapFrom(AdmissionDto admissionDto) {
        return this.modelMapper.map(admissionDto, Admission.class);
    }

    public Admission mapFrom(AdmissionRequestDto admissionRequestDto) {
        return this.modelMapper.map(admissionRequestDto, Admission.class);
    }
}
