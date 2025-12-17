package com.pulseclinic.pulse_server.security.service;

import com.pulseclinic.pulse_server.mappers.impl.RoleMapper;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.dto.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager, RoleRepository roleRepository,
                       OtpService otpService, EmailService emailService, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.tokenBlacklistService = tokenBlacklistService;
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
                .fullName(registerRequest.getFull_name())
                .birthDate(registerRequest.getBirth_date())
                .gender(registerRequest.getGender())
                .hashedPassword(passwordEncoder.encode(registerRequest.getPassword()))
                .role(roleRepository.findByName(registerRequest.getRoleDto().getName()).orElseThrow())
                .citizenId(registerRequest.getCitizen_id())
                .phone(registerRequest.getPhone())
                .avatarUrl(registerRequest.getAvatar_url())
                .isActive(true)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }

    public Boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // check for otp
        otpService.verifyOtp(resetPasswordRequest.getEmail() ,resetPasswordRequest.getOtp());

        // create specific token for reset password
//        User existingUser = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(() -> new RuntimeException("User doesn't exist"));
//        String resetToken = generateResetToken(existingUser);
//        Claims claims = jwtService.extractAllClaims(resetToken);
//        if (!"reset_password".equals(claims.get("purpose"))) {
//            throw new RuntimeException("Invalid token purpose");
//        }
//        String email = claims.getSubject();
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(() -> new RuntimeException("User doesn't exist"));
        user.setHashedPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public Boolean generateResetToken(ForgotPasswordRequest forgotPasswordRequest) {
//        Map<String, Object> extraClaims = new HashMap<>();
//        extraClaims.put("purpose", "reset_password");
//        extraClaims.put("email", user.getEmail());
//        String resetToken = jwtService.generateToken(extraClaims, user);
        // check if user exist
        User existingUser = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(() -> new RuntimeException("User doesn't exist"));
        // todo: gen otp
        String otp = otpService.requestOtp(forgotPasswordRequest.getEmail());
        // todo: send email
        emailService.sendPasswordResetOtp(forgotPasswordRequest.getEmail(), otp);
        return true;
    }

    public Boolean logout(String email, String token) {
        String subject = jwtService.extractUsername(token); // extract token to see if mail match
        if (!email.equalsIgnoreCase(subject)) {
            throw new RuntimeException("Invalid token");
        }

        long ttlSeconds = jwtService.getRemainingTtlSeconds(token);
        if (ttlSeconds <= 0) return false;
        tokenBlacklistService.blacklistToken(token, ttlSeconds);
        return true;
    }

}
