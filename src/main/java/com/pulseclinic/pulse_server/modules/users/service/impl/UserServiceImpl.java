package com.pulseclinic.pulse_server.modules.users.service.impl;

import com.pulseclinic.pulse_server.modules.users.dto.user.UserRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.modules.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
    public User update(String email, UserRequestDto userRequestDto) {
        Optional<User> user = this.findByEmail(email);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        if (userRequestDto.getGender() != null){
            user.get().setGender(userRequestDto.getGender());
        }
//        if (userRequestDto.getEmail() != null){
//            user.get().setEmail(userRequestDto.getEmail());
//        }
        if (userRequestDto.getAddress() != null){
            user.get().setAddress(userRequestDto.getAddress());
        }
        if (userRequestDto.getPhone() != null){
            user.get().setPhone(userRequestDto.getPhone());
        }
        if (userRequestDto.getBirth_date() != null){
            user.get().setBirthDate(userRequestDto.getBirth_date());
        }
        if (userRequestDto.getAvatar_url() != null){
            user.get().setAvatarUrl(userRequestDto.getAvatar_url());
        }
        if (userRequestDto.getCitizen_id() != null){
            user.get().setCitizenId(userRequestDto.getCitizen_id());
        }
        if (userRequestDto.getFull_name() != null){
            user.get().setFullName(userRequestDto.getFull_name());
        }
        if (userRequestDto.getRole_id() != null){
            Optional<Role> role = this.roleRepository.findById(userRequestDto.getRole_id());
            if (role.isPresent()){
                user.get().setRole(role.get());
            }
        }
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }

    @Override
    public User updateAvatar(String email, String avatarUrl) {
        Optional<User> user = this.findByEmail(email);
        if (user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        user.get().setAvatarUrl(avatarUrl);
        User savedUser = this.userRepository.save(user.get());
        return savedUser;
    }

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
}
