package com.pulseclinic.pulse_server.security.service;

import com.pulseclinic.pulse_server.mappers.impl.RoleMapper;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.dto.AuthResponse;
import com.pulseclinic.pulse_server.security.dto.LoginRequest;
import com.pulseclinic.pulse_server.security.dto.RegisterRequest;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleMapper roleMapper, JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }


    public AuthResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .full_name(registerRequest.getFull_name())
                .birth_date(registerRequest.getBirth_date())
                .gender(registerRequest.getGender())
                .hashed_password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(roleMapper.mapFrom(registerRequest.getRoleDto()))
                .citizen_id(registerRequest.getCitizen_id())
                .phone(registerRequest.getPhone())
                .avatar_url(registerRequest.getAvatar_url())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }
}
