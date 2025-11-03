package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public User mapFrom(UserRequestDto userRequestDto) {
        return this.modelMapper.map(userRequestDto, User.class);
    }
}
