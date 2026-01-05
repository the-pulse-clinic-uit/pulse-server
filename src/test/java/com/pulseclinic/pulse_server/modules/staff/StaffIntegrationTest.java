package com.pulseclinic.pulse_server.modules.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.service.JwtService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Staff Integration Tests - Success Cases Only")
class StaffIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    // Test data
    private Role doctorRole;
    private Role staffRole;

    private User doctorUser;
    private User staffUser;
    private User newStaffUser;

    private String doctorToken;
    private String staffToken;
    private String newStaffToken;

    private Staff stDoctor;
    private Staff stStaff;
    private Staff stNewStaff;

    private Department cardiology;
    private Department emergency;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        // Clean database
        entityManager.createNativeQuery("DELETE FROM doctors").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM staff").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM patients").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM rooms").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM departments").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM roles").executeUpdate();

        // Create roles
        doctorRole = Role.builder().name("DOCTOR").build();
        staffRole = Role.builder().name("STAFF").build();

        doctorRole = roleRepository.save(doctorRole);
        staffRole = roleRepository.save(staffRole);

        // Create departments
        cardiology = Department.builder()
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .build();

        emergency = Department.builder()
                .name("Emergency")
                .description("Emergency department")
                .build();

        cardiology = departmentRepository.save(cardiology);
        emergency = departmentRepository.save(emergency);

        // Create users with different roles
        doctorUser = User.builder()
                .email("doctor@example.com")
                .fullName("Dr. John Smith")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0123456789")
                .citizenId("123456789012")
                .birthDate(LocalDate.of(1985, 5, 15))
                .gender(true) // Male
                .address("123 Doctor Street")
                .isActive(true)
                .build();

        staffUser = User.builder()
                .email("staff@example.com")
                .fullName("Staff Member")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(staffRole)
                .phone("0123456790")
                .citizenId("123456789013")
                .birthDate(LocalDate.of(1990, 3, 20))
                .gender(false) // Female
                .address("456 Staff Street")
                .isActive(true)
                .build();

        // User for creating new staff
        newStaffUser = User.builder()
                .email("newstaff@example.com")
                .fullName("New Staff Member")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(staffRole)
                .phone("0987654321")
                .citizenId("987654321012")
                .birthDate(LocalDate.of(1992, 7, 20))
                .gender(false)
                .address("321 New Street")
                .isActive(true)
                .build();

        doctorUser = userRepository.save(doctorUser);
        staffUser = userRepository.save(staffUser);
        newStaffUser = userRepository.save(newStaffUser);

        doctorToken = jwtService.generateToken(doctorUser);
        staffToken = jwtService.generateToken(staffUser);
        newStaffToken = jwtService.generateToken(newStaffUser);

        stDoctor = Staff.builder()
                .user(doctorUser)
                .position(Position.DOCTOR)
                .createdAt(LocalDateTime.now())
                .build();

        stStaff = Staff.builder()
                .user(staffUser)
                .position(Position.STAFF)
                .createdAt(LocalDateTime.now())
                .build();

//        stNewStaff = Staff.builder()
//                .user(newStaffUser)
//                .position(Position.STAFF)
//                .createdAt(LocalDateTime.now())
//                .build();

        stStaff = staffRepository.save(stStaff);
//        stNewStaff = staffRepository.save(stNewStaff);
        stDoctor = staffRepository.save(stDoctor);

        // Create existing staff
//        testStaff = Staff.builder()
//                .user(staffUser)
//                .position(Position.STAFF)
//                .department(cardiology)
//                .build();
//        testStaff = staffRepository.save(testStaff);
    }

    // ==================== BACKEND_STAFF_001 ====================
    @Test
    @DisplayName("BACKEND_STAFF_001: Create new staff member with doctor role")
    void whenCreateStaffWithDoctorRole_thenStaffCreated() throws Exception {
        // Given
        StaffRequestDto request = StaffRequestDto.builder()
                .userId(newStaffUser.getId())
                .position(Position.STAFF)
                .build();

        // When & Then
        mockMvc.perform(post("/staff")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.position").value("STAFF"))
                .andExpect(jsonPath("$.userDto").exists())
                .andExpect(jsonPath("$.userDto.email").value("newstaff@example.com"))
                .andExpect(jsonPath("$.userDto.fullName").value("New Staff Member"));

        // Verify staff created in database
        Optional<Staff> savedStaff = staffRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(newStaffUser.getId()))
                .findFirst();

        assertThat(savedStaff).isPresent();
        assertThat(savedStaff.get().getPosition()).isEqualTo(Position.STAFF);
        assertThat(savedStaff.get().getUser().getEmail()).isEqualTo("newstaff@example.com");
    }

    @Test
    @DisplayName("BACKEND_STAFF_001: Create staff with DOCTOR position")
    void whenCreateStaffWithDoctorPosition_thenStaffCreated() throws Exception {
        // Given
        StaffRequestDto request = StaffRequestDto.builder()
                .userId(newStaffUser.getId())
                .position(Position.DOCTOR)
                .build();

        // When & Then
        mockMvc.perform(post("/staff")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.position").value("DOCTOR"));
    }

    // ==================== BACKEND_STAFF_002 ====================
    @Test
    @DisplayName("BACKEND_STAFF_002: Search staff by STAFF position")
    void whenSearchStaffByStaffPosition_thenReturnMatchingStaff() throws Exception {
        // When & Then
        mockMvc.perform(get("/staff/search")
                        .header("Authorization", "Bearer " + staffToken)
                        .param("position", "STAFF"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].position").value("STAFF"))
                .andExpect(jsonPath("$[0].userDto").exists());
    }

    @Test
    @DisplayName("BACKEND_STAFF_002: Search staff by DOCTOR position")
    void whenSearchStaffByDoctorPosition_thenReturnMatchingStaff() throws Exception {
        // Given - Create a staff with DOCTOR position
        User doctorStaffUser = User.builder()
                .email("doctorstaff@example.com")
                .fullName("Doctor Staff")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0111111111")
                .citizenId("111111111111")
                .birthDate(LocalDate.of(1988, 4, 15))
                .gender(true)
                .isActive(true)
                .build();
        doctorStaffUser = userRepository.save(doctorStaffUser);

        Staff doctorStaff = Staff.builder()
                .user(doctorStaffUser)
                .position(Position.DOCTOR)
                .department(emergency)
                .build();
        staffRepository.save(doctorStaff);

        // When & Then
        mockMvc.perform(get("/staff/search")
                        .header("Authorization", "Bearer " + staffToken)
                        .param("position", "DOCTOR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].position").value("DOCTOR"));
    }

//    @Test
//    @DisplayName("BACKEND_STAFF_002: Search staff does not require authentication")
//    void whenSearchStaffWithoutAuth_thenSuccess() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/staff/search")
//                        .param("position", "STAFF"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray());
//    }

    // ==================== BACKEND_STAFF_003 ====================
    @Test
    @DisplayName("BACKEND_STAFF_003: Get staff member by ID")
    void whenGetStaffById_thenReturnStaff() throws Exception {
        // When & Then
        mockMvc.perform(get("/staff/{id}", stStaff.getId())
                        .header("Authorization", "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stStaff.getId().toString()))
                .andExpect(jsonPath("$.position").value("STAFF"))
                .andExpect(jsonPath("$.userDto").exists())
                .andExpect(jsonPath("$.userDto.fullName").value(stStaff.getUser().getFullName()))
                .andExpect(jsonPath("$.userDto.email").value(stStaff.getUser().getEmail()));
    }

//    @Test
//    @DisplayName("BACKEND_STAFF_003: Get staff by ID does not require authentication")
//    void whenGetStaffByIdWithoutAuth_thenSuccess() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/staff/{id}", testStaff.getId()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").exists());
//    }

    // ==================== BACKEND_STAFF_004 ====================
    @Test
    @DisplayName("BACKEND_STAFF_004: Staff member gets own profile")
    void whenStaffGetsOwnProfile_thenReturnProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/staff/me")
//                        .with(jwt().authorities(new SimpleGrantedAuthority("staff"))))
                        .header("Authorization", "Bearer " + staffToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stStaff.getId().toString()))
                .andExpect(jsonPath("$.position").value("STAFF"))
                .andExpect(jsonPath("$.userDto.email").value(stStaff.getUser().getEmail()))
                .andExpect(jsonPath("$.userDto.fullName").value(stStaff.getUser().getFullName()));
    }

    @Test
    @DisplayName("BACKEND_STAFF_004: Doctor user gets own staff profile")
    void whenDoctorGetsOwnProfile_thenReturnProfile() throws Exception {

        // When & Then
        mockMvc.perform(get("/staff/me")
//                        .with(jwt().authorities(new SimpleGrantedAuthority("staff"))))
                        .header("Authorization", "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stDoctor.getId().toString()))
                .andExpect(jsonPath("$.position").value("DOCTOR"))
                .andExpect(jsonPath("$.userDto.email").value(stDoctor.getUser().getEmail()))
                .andExpect(jsonPath("$.userDto.fullName").value(stDoctor.getUser().getFullName()));
    }

    // ==================== BACKEND_STAFF_005 ====================
    @Test
    @DisplayName("BACKEND_STAFF_005: Update staff own profile successfully")
    void whenStaffUpdatesOwnProfile_thenProfileUpdated() throws Exception {
        // Given
        StaffDto updateDto = StaffDto.builder()
                .position(Position.DOCTOR) // Update position
                .build();

        // When & Then
        mockMvc.perform(patch("/staff/me")
                        .header("Authorization", "Bearer " + staffToken)
//                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("DOCTOR"))
                .andExpect(jsonPath("$.id").value(stStaff.getId().toString()));

        // Verify in database
        Staff updatedStaff = staffRepository.findById(stStaff.getId()).orElseThrow();
        assertThat(updatedStaff.getPosition()).isEqualTo(Position.DOCTOR);
    }
}