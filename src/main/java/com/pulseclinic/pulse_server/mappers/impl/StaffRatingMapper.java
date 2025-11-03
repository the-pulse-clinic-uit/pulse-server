package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.ratings.dto.StaffRatingDto;
import com.pulseclinic.pulse_server.modules.ratings.dto.StaffRatingRequestDto;
import com.pulseclinic.pulse_server.modules.ratings.entity.StaffRating;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StaffRatingMapper implements Mapper<StaffRating, StaffRatingDto> {
    private final ModelMapper modelMapper;

    public StaffRatingMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public StaffRatingDto mapTo(StaffRating staffRating) {
        return this.modelMapper.map(staffRating, StaffRatingDto.class);
    }

    @Override
    public StaffRating mapFrom(StaffRatingDto staffRatingDto) {
        return this.modelMapper.map(staffRatingDto, StaffRating.class);
    }

    public StaffRating mapFrom(StaffRatingRequestDto staffRatingRequestDto) {
        return this.modelMapper.map(staffRatingRequestDto, StaffRating.class);
    }
}
