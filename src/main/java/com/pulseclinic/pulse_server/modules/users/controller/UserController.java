package com.pulseclinic.pulse_server.modules.users.controller;

import com.pulseclinic.pulse_server.mappers.impl.RoleMapper;
import com.pulseclinic.pulse_server.mappers.impl.UserMapper;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserRequestDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.service.RoleService;
import com.pulseclinic.pulse_server.modules.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public UserController(UserService userService, UserMapper userMapper, RoleMapper roleMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;

    }

    // @PatchMapping("/me/avatar")
    // public ResponseEntity<UserDto> updateAvatar(Authentication authentication,
    // @RequestParam("avatar") String avatarUrl) {
    // String email = authentication.getName();
    // User user = userService.updateAvatar(email, avatarUrl);
    // return new ResponseEntity<>(this.userMapper.mapTo(user), HttpStatus.OK);
    // }

    @PatchMapping("/me/avatar")
    public ResponseEntity<UserDto> updateAvatar(Authentication authentication, @RequestParam("file") MultipartFile file)
            throws IOException {
        User user = this.userService.updateAvatar(authentication.getName(), file);
        return new ResponseEntity<>(this.userMapper.mapTo(user), HttpStatus.OK);

    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updatePersonalInfo(Authentication authentication, @RequestBody UserDto userDto) {
        String email = authentication.getName();
        User user = userService.update(email, userDto);
        return new ResponseEntity<>(this.userMapper.mapTo(user), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable UUID id, RoleDto roleDto) {
        Role role = this.roleMapper.mapFrom(roleDto);
        User user = this.userService.updateRole(id, role);
        return new ResponseEntity<>(this.userMapper.mapTo(user), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = this.userService.findByEmail(email);
        return user.map(value -> new ResponseEntity<>(this.userMapper.mapTo(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable UUID id) {
        User user = this.userService.deactivateUser(id);
        return new ResponseEntity<>(userMapper.mapTo(user), HttpStatus.OK);
    }

    @GetMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<UserDto> activateUser(@PathVariable UUID id) {
        User user = this.userService.activateUser(id);
        return new ResponseEntity<>(userMapper.mapTo(user), HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> allUsers = this.userService.findAll();
        return new ResponseEntity<>(allUsers.stream().map(user -> userMapper.mapTo(user)).collect(Collectors.toList()),
                HttpStatus.OK);
    }

}
