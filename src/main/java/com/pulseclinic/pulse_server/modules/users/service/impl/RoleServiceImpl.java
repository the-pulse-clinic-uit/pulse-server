package com.pulseclinic.pulse_server.modules.users.service.impl;

import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.service.RoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        return this.roleRepository.save(role);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        return Optional.ofNullable(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findByName(String name) {
        Role role = this.roleRepository.findByName(name).orElse(null);
        return Optional.ofNullable(role);
    }

    @Override
    public Boolean deleteById(UUID id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        if (role != null) {
            role.setDeletedAt(LocalDateTime.now());
            this.roleRepository.save(role);
            return true;
        }
        else return false;
    }


}
