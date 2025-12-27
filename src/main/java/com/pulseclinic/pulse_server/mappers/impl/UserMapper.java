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
    private final RoleMapper roleMapper;

    public UserMapper(ModelMapper modelMapper, RoleMapper roleMapper) {
        this.modelMapper = modelMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDto mapTo(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .hashedPassword(user.getHashedPassword())
                .fullName(user.getFullName())
                .address(user.getAddress())
                .citizenId(user.getCitizenId())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .roleDto(user.getRole() != null ? roleMapper.mapTo(user.getRole()) : null)
                .build();
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public User mapFrom(UserRequestDto userRequestDto) {
        return this.modelMapper.map(userRequestDto, User.class);
    }
}
