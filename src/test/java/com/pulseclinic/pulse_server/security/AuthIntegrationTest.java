package com.pulseclinic.pulse_server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.modules.users.dto.role.RoleDto;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.dto.*;
import com.pulseclinic.pulse_server.security.service.EmailService;
import com.pulseclinic.pulse_server.security.service.JwtService;
import com.pulseclinic.pulse_server.security.service.OtpService;
import com.pulseclinic.pulse_server.security.service.TokenBlacklistService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private OtpService otpService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private Role patientRole;
    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Clean database
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM prescription_details").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM prescriptions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM encounters").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM drugs").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM patients").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM doctors").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM staff").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM roles").executeUpdate();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        // Create test role
        patientRole = new Role();
        patientRole.setName("PATIENT");
        patientRole = roleRepository.save(patientRole);

        // Create test user
        testUser = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(patientRole)
                .phone("0123456789")
                .citizenId("123456789012")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .address("123 Test Street")
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        // Generate valid token for authenticated tests
        validToken = jwtService.generateToken(testUser);
    }

    // ==================== BACKEND_AUTH_001 ====================
    @Test
    @DisplayName("BACKEND_AUTH_001: Register new user with valid credentials")
    void whenRegisterWithValidCredentials_thenUserCreatedAndTokenReturned() throws Exception {
        // Given
        RoleDto roleDto = new RoleDto();
        roleDto.setName("PATIENT");

        RegisterRequest request = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("Password123!")
                .full_name("New User")
                .citizen_id("987654321012")
                .phone("0987654321")
                .birth_date(LocalDate.of(1995, 5, 15))
                .gender(false) // Boolean: false = Female, true = Male
                .address("456 New Street")
                .avatar_url("https://example.com/avatar.jpg")
                .roleDto(roleDto)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // Verify user created in database
        Optional<User> savedUser = userRepository.findByEmail("newuser@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getFullName()).isEqualTo("New User");
        assertThat(savedUser.get().getCitizenId()).isEqualTo("987654321012");

        // Verify password is hashed
        assertThat(savedUser.get().getHashedPassword()).isNotEqualTo("Password123!");
        assertThat(passwordEncoder.matches("Password123!", savedUser.get().getHashedPassword())).isTrue();
    }

    // ==================== BACKEND_AUTH_002 ====================
    @Test
    @DisplayName("BACKEND_AUTH_002: Login with valid credentials")
    void whenLoginWithValidCredentials_thenAuthenticationSuccessful() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // ==================== BACKEND_AUTH_003 ====================
    @Test
    @DisplayName("BACKEND_AUTH_003: Login with invalid email (FAIL)")
    void whenLoginWithNonExistentEmail_thenUnauthorized() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("Password123!")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ==================== BACKEND_AUTH_004 ====================
    @Test
    @DisplayName("BACKEND_AUTH_004: Login with incorrect password (FAIL)")
    void whenLoginWithWrongPassword_thenUnauthorized() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword!")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ==================== BACKEND_AUTH_005 ====================
    @Test
    @DisplayName("BACKEND_AUTH_005: Request password reset with valid email")
    void whenRequestPasswordResetWithValidEmail_thenOtpSentSuccessfully() throws Exception {
        // Given
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("test@example.com")
                .build();

        when(otpService.requestOtp(anyString())).thenReturn("123456");
        doNothing().when(emailService).sendPasswordResetOtp(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify OTP service was called
        verify(otpService, times(1)).requestOtp("test@example.com");
        verify(emailService, times(1)).sendPasswordResetOtp(eq("test@example.com"), eq("123456"));
    }

    // ==================== BACKEND_AUTH_006 ====================
    @Test
    @DisplayName("BACKEND_AUTH_006: Request password reset with non-existent email")
    void whenRequestPasswordResetWithNonExistentEmail_thenUnauthorized() throws Exception {
        // Given
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("nonexistent@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify no OTP was generated
        verify(otpService, never()).requestOtp(anyString());
        verify(emailService, never()).sendPasswordResetOtp(anyString(), anyString());
    }

    // ==================== BACKEND_AUTH_007 ====================
    @Test
    @DisplayName("BACKEND_AUTH_007: Reset password with valid OTP")
    void whenResetPasswordWithValidOtp_thenPasswordUpdatedSuccessfully() throws Exception {
        // Given
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@example.com")
                .otp("123456")
                .newPassword("NewPassword123!")
                .build();

        doNothing().when(otpService).verifyOtp(anyString(), anyString());

        String oldPasswordHash = testUser.getHashedPassword();

        // When & Then
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify password was changed in database
        User updatedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(updatedUser.getHashedPassword()).isNotEqualTo(oldPasswordHash);
        assertThat(passwordEncoder.matches("NewPassword123!", updatedUser.getHashedPassword())).isTrue();

        // Verify old password no longer works
        assertThat(passwordEncoder.matches("Password123!", updatedUser.getHashedPassword())).isFalse();
    }

    // ==================== BACKEND_AUTH_008 ====================
    @Test
    @DisplayName("BACKEND_AUTH_008: Reset password with expired/invalid OTP (FAIL)")
    void whenResetPasswordWithExpiredOtp_thenUnauthorized() throws Exception {
        // Given
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@example.com")
                .otp("expired-otp")
                .newPassword("NewPassword123!")
                .build();

        doThrow(new RuntimeException("OTP expired or invalid"))
                .when(otpService).verifyOtp(anyString(), anyString());

        String originalPasswordHash = testUser.getHashedPassword();

        // When & Then
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify password was NOT changed
        User unchangedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(unchangedUser.getHashedPassword()).isEqualTo(originalPasswordHash);
    }

    // ==================== BACKEND_AUTH_009 ====================
//    @Test
//    @DisplayName("BACKEND_AUTH_009: Logout user and invalidate token")
//    void whenLogoutWithValidToken_thenTokenBlacklisted() throws Exception {
//        // Given
//        when(tokenBlacklistService.isBlacklisted(anyString())).thenReturn(true);
//
//        // When & Then
//        mockMvc.perform(post("/auth/logout")
////                        .header("Authorization", "Bearer " + validToken))
////                        .contentType(MediaType.APPLICATION_JSON))
//                        .with(jwt()))
//
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        // Verify token was blacklisted
//        verify(tokenBlacklistService, times(1)).blacklistToken(eq(validToken), anyLong());
//    }

    // ==================== BACKEND_AUTH_010 ====================
//    @Test
//    @DisplayName("BACKEND_AUTH_010: Attempt logout without authentication (FAIL)")
//    void whenLogoutWithoutAuthentication_thenUnauthorized() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/auth/logout")
//                        .with(jwt().jwt(j -> j.claim("sub", testUser.getEmail()))))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//
//        // Verify no blacklist operation occurred
//        verify(tokenBlacklistService, never()).blacklistToken(anyString(), anyLong());
//    }
}