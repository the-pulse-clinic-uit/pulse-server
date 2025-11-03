package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements Mapper<Role, RoleDto> {
    private final ModelMapper modelMapper;

    public RoleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public RoleDto mapTo(Role role) {
        return this.modelMapper.map(role, RoleDto.class);
    }

    @Override
    public Role mapFrom(RoleDto roleDto) {
        return this.modelMapper.map(roleDto, Role.class);
    }

    public Role mapFrom(RoleRequestDto roleRequestDto) {
        return this.modelMapper.map(roleRequestDto, Role.class);
    }
}
