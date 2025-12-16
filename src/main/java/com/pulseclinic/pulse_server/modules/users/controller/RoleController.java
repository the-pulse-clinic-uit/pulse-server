package com.pulseclinic.pulse_server.modules.users.controller;

import com.pulseclinic.pulse_server.mappers.impl.RoleMapper;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class RoleController {
    private final RoleMapper roleMapper;
    private final RoleService roleService;
    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @PostMapping(path = "/roles")
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleRequestDto roleRequestDto) {
        Role role = this.roleMapper.mapFrom(roleRequestDto);
        return new ResponseEntity<>(this.roleMapper.mapTo(this.roleService.create(role)), HttpStatus.CREATED);
    }

    @GetMapping(path = "/roles/{id}")
    public ResponseEntity<RoleDto> findById(@PathVariable UUID id) {
        Optional<Role> role = this.roleService.findById(id);
        if (role.isPresent()) {
            RoleDto roleDto = this.roleMapper.mapTo(role.get());
            return ResponseEntity.ok(roleDto);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path="/roles")
    public ResponseEntity<List<RoleDto>> findAll() {
        List<Role> roles = this.roleService.findAll();
        List<RoleDto> roleDtos = roles.stream()
                .map(roleMapper::mapTo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }
}
