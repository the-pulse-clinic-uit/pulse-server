package com.pulseclinic.pulse_server.modules.users.service;

import com.pulseclinic.pulse_server.modules.users.dto.user.UserRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);

    List<User> findAll();

    User update(String email, UserRequestDto userRequestDto);
    User updateAvatar(String email, String avatarUrl);
    User deactivateUser(UUID id);
    User activateUser(UUID id);

}
