package com.pulseclinic.pulse_server.modules.users.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;

public interface UserService {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);

    List<User> findAll();

    User update(String email, UserDto userDto);
    //User updateAvatar(String email, String avatarUrl);
    User updateAvatar(String email, MultipartFile file) throws IOException;
    User deactivateUser(UUID id);
    User activateUser(UUID id);
    User updateRole(UUID id, Role role);

}
