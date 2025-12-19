package com.pulseclinic.pulse_server.modules.users.service.impl;

import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.modules.users.service.UserService;
import com.pulseclinic.pulse_server.services.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StorageService storageService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository
    , StorageService storageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storageService = storageService;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User update(String email, UserDto userDto) {
        Optional<User> user = this.findByEmail(email);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        if (userDto.getGender() != null){
            user.get().setGender(userDto.getGender());
        }
//        if (userDto.getEmail() != null){
//            user.get().setEmail(userDto.getEmail());
//        }
        if (userDto.getAddress() != null){
            user.get().setAddress(userDto.getAddress());
        }
        if (userDto.getPhone() != null){
            user.get().setPhone(userDto.getPhone());
        }
        if (userDto.getBirthDate() != null){
            user.get().setBirthDate(userDto.getBirthDate());
        }
        if (userDto.getAvatarUrl() != null){
            user.get().setAvatarUrl(userDto.getAvatarUrl());
        }
        if (userDto.getCitizenId() != null){
            user.get().setCitizenId(userDto.getCitizenId());
        }
        if (userDto.getFullName() != null){
            user.get().setFullName(userDto.getFullName());
        }
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }

    @Override
    public User updateAvatar(String email, MultipartFile file) throws IOException {
        Optional<User> user = this.findByEmail(email);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User foundUser = user.get();
        String avatarUrl = this.storageService.uploadAvatar(file, foundUser.getId());

        foundUser.setAvatarUrl(avatarUrl);
        User savedUser = this.userRepository.save(foundUser);
        return savedUser;
    }

//    @Override
//    public User updateAvatar(String email, String avatarUrl) {
//        Optional<User> user = this.findByEmail(email);
//        if (user.isEmpty()){
//            throw new RuntimeException("User not found");
//        }
//        user.get().setAvatarUrl(avatarUrl);
//        User savedUser = this.userRepository.save(user.get());
//        return savedUser;
//    }

    @Override
    public User deactivateUser(UUID id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        user.get().setIsActive(false);
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }

    @Override
    public User activateUser(UUID id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        user.get().setIsActive(true);
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }

    @Override
    public User updateRole(UUID id, Role role){
        Optional<User> user = this.userRepository.findById(id);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        Optional<Role> foundRole = this.roleRepository.findById(role.getId());
        if (foundRole.isPresent()){
            user.get().setRole(foundRole.get());
        }
        user.get().setRole(role);
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }
}
