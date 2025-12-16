package com.pulseclinic.pulse_server.modules.users.service;

import com.pulseclinic.pulse_server.modules.users.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    Role create(Role role);
    Optional<Role> findById(UUID id);
    List<Role> findAll();
    Optional<Role> findByName(String name);
    Boolean deleteById(UUID id);
}
