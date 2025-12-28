package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FollowUpPlanMapper implements Mapper<FollowUpPlan, FollowUpPlanDto> {
    private final ModelMapper modelMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final EncounterMapper encounterMapper;

    public FollowUpPlanMapper(ModelMapper modelMapper, PatientMapper patientMapper,
                            DoctorMapper doctorMapper, EncounterMapper encounterMapper) {
        this.modelMapper = modelMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
        this.encounterMapper = encounterMapper;
    }

    @Override
    public FollowUpPlanDto mapTo(FollowUpPlan followUpPlan) {
        return FollowUpPlanDto.builder()
                .id(followUpPlan.getId())
                .firstDueAt(followUpPlan.getFirstDueAt())
                .rrule(followUpPlan.getRrule())
                .status(followUpPlan.getStatus())
                .notes(followUpPlan.getNotes())
                .createdAt(followUpPlan.getCreatedAt())
                .patientDto(followUpPlan.getPatient() != null ? patientMapper.mapTo(followUpPlan.getPatient()) : null)
                .doctorDto(followUpPlan.getDoctor() != null ? doctorMapper.mapTo(followUpPlan.getDoctor()) : null)
                .baseEncounterDto(followUpPlan.getBaseEncounter() != null ? encounterMapper.mapTo(followUpPlan.getBaseEncounter()) : null)
                .build();
    }

    @Override
    public FollowUpPlan mapFrom(FollowUpPlanDto followUpPlanDto) {
        return this.modelMapper.map(followUpPlanDto, FollowUpPlan.class);
    }

    public FollowUpPlan mapFrom(FollowUpPlanRequestDto followUpPlanRequestDto) {
        return this.modelMapper.map(followUpPlanRequestDto, FollowUpPlan.class);
    }
}
