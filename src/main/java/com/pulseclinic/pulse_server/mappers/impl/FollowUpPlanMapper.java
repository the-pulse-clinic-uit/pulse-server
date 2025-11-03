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

    public FollowUpPlanMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public FollowUpPlanDto mapTo(FollowUpPlan followUpPlan) {
        return this.modelMapper.map(followUpPlan, FollowUpPlanDto.class);
    }

    @Override
    public FollowUpPlan mapFrom(FollowUpPlanDto followUpPlanDto) {
        return this.modelMapper.map(followUpPlanDto, FollowUpPlan.class);
    }

    public FollowUpPlan mapFrom(FollowUpPlanRequestDto followUpPlanRequestDto) {
        return this.modelMapper.map(followUpPlanRequestDto, FollowUpPlan.class);
    }
}
