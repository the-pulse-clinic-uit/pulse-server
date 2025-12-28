package com.pulseclinic.pulse_server.modules.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Test data
    private Role doctorRole;
    private Role staffRole;
    private Role patientRole;

    private User doctorUser;
    private User staffUser;
    private User patientUser;

    private Department cardiology;
    private Department neurology;

    private Staff testStaff;
    private Staff anotherStaff;

    private Doctor testDoctor;

    private String doctorToken;
    private String staffToken;
    private String patientToken;

    @BeforeEach
    void setUp() {
        // Clean database
        doctorRepository.deleteAll();
        staffRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        doctorRole = Role.builder().name("DOCTOR").build();
        staffRole = Role.builder().name("STAFF").build();
        patientRole = Role.builder().name("PATIENT").build();

        doctorRole = roleRepository.save(doctorRole);
        staffRole = roleRepository.save(staffRole);
        patientRole = roleRepository.save(patientRole);

        // Create departments
        cardiology = Department.builder()
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .build();

        neurology = Department.builder()
                .name("Neurology")
                .description("Brain and nervous system")
                .build();

        cardiology = departmentRepository.save(cardiology);
        neurology = departmentRepository.save(neurology);

        // Create users with different roles
        doctorUser = User.builder()
                .email("doctor@example.com")
                .fullName("Dr. John Smith")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0123456789")
                .citizenId("123456789012")
                .birthDate(LocalDate.of(1985, 5, 15))
                .gender(false)
                .isActive(true)
                .build();

        staffUser = User.builder()
                .email("staff@example.com")
                .fullName("Staff User")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(staffRole)
                .phone("0123456790")
                .citizenId("123456789013")
                .birthDate(LocalDate.of(1990, 3, 20))
                .gender(true)
                .isActive(true)
                .build();

        patientUser = User.builder()
                .email("patient@example.com")
                .fullName("Patient User")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(patientRole)
                .phone("0123456791")
                .citizenId("123456789014")
                .birthDate(LocalDate.of(1995, 8, 10))
                .gender(true)
                .isActive(true)
                .build();

        doctorUser = userRepository.save(doctorUser);
        staffUser = userRepository.save(staffUser);
        patientUser = userRepository.save(patientUser);

        // Create staff linked to doctor user
        testStaff = Staff.builder()
                .user(doctorUser)
                .department(cardiology)
                .position(Position.DOCTOR)
                .build();
        testStaff = staffRepository.save(testStaff);

        // Create another staff for creating new doctor
        User anotherUser = User.builder()
                .email("newdoctor@example.com")
                .fullName("Dr. Jane Doe")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0987654321")
                .citizenId("987654321012")
                .birthDate(LocalDate.of(1988, 9, 25))
                .gender(false)
                .isActive(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        anotherStaff = Staff.builder()
                .user(anotherUser)
                .department(neurology)
                .position(Position.DOCTOR)
                .build();
        anotherStaff = staffRepository.save(anotherStaff);

        // Create existing doctor
        testDoctor = Doctor.builder()
                .licenseId("LIC-12345")
                .isVerified(true)
                .staff(testStaff)
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        // Generate JWT tokens for each role
        doctorToken = jwtService.generateToken(doctorUser);
        staffToken = jwtService.generateToken(staffUser);
        patientToken = jwtService.generateToken(patientUser);
    }

    // ==================== BACKEND_DOCTOR_001 ====================
    @Test
    @DisplayName("BACKEND_DOCTOR_001: Create new doctor with valid data")
    void whenCreateDoctorWithValidData_thenDoctorCreated() throws Exception {
        // Given
        DoctorRequestDto request = DoctorRequestDto.builder()
                .licenseId("LIC-67890")
                .isVerified(false)
                .staffId(anotherStaff.getId())
                .departmentId(neurology.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/doctors")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.licenseId").value("LIC-67890"))
                .andExpect(jsonPath("$.isVerified").value(false))
                .andExpect(jsonPath("$.staffDto").exists())
                .andExpect(jsonPath("$.departmentDto").exists());

        // Verify doctor created in database
        Optional<Doctor> savedDoctor = doctorRepository.findAll().stream()
                .filter(d -> "LIC-67890".equals(d.getLicenseId()))
                .findFirst();

        assertThat(savedDoctor).isPresent();
        assertThat(savedDoctor.get().getLicenseId()).isEqualTo("LIC-67890");
        assertThat(savedDoctor.get().getStaff().getId()).isEqualTo(anotherStaff.getId());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_001: Create doctor without authentication should fail")
    void whenCreateDoctorWithoutAuth_thenUnauthorized() throws Exception {
        // Given
        DoctorRequestDto request = DoctorRequestDto.builder()
                .licenseId("LIC-99999")
                .isVerified(false)
                .staffId(anotherStaff.getId())
                .departmentId(neurology.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_001: Create doctor with patient role should fail")
    void whenCreateDoctorWithPatientRole_thenForbidden() throws Exception {
        // Given
        DoctorRequestDto request = DoctorRequestDto.builder()
                .licenseId("LIC-88888")
                .isVerified(false)
                .staffId(anotherStaff.getId())
                .departmentId(neurology.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/doctors")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_001: Create doctor with duplicate licenseId should fail")
    void whenCreateDoctorWithDuplicateLicenseId_thenBadRequest() throws Exception {
        // Given
        DoctorRequestDto request = DoctorRequestDto.builder()
                .licenseId("LIC-12345") // Already exists
                .isVerified(false)
                .staffId(anotherStaff.getId())
                .departmentId(neurology.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/doctors")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ==================== BACKEND_DOCTOR_002 ====================
    @Test
    @DisplayName("BACKEND_DOCTOR_002: Get all doctors with doctor role")
    void whenGetAllDoctorsWithDoctorRole_thenReturnAllDoctors() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors")
                        .header("Authorization", "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].licenseId").exists())
                .andExpect(jsonPath("$[0].staffDto").exists());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_002: Get all doctors with staff role")
    void whenGetAllDoctorsWithStaffRole_thenReturnAllDoctors() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors")
                        .header("Authorization", "Bearer " + staffToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_002: Get all doctors with patient role")
    void whenGetAllDoctorsWithPatientRole_thenReturnAllDoctors() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors")
                        .header("Authorization", "Bearer " + patientToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_002: Get all doctors without authentication should fail")
    void whenGetAllDoctorsWithoutAuth_thenUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ==================== BACKEND_DOCTOR_003 ====================
    @Test
    @DisplayName("BACKEND_DOCTOR_003: Get doctor by ID with valid ID")
    void whenGetDoctorByIdWithValidId_thenReturnDoctor() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}", testDoctor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDoctor.getId().toString()))
                .andExpect(jsonPath("$.licenseId").value("LIC-12345"))
                .andExpect(jsonPath("$.isVerified").value(true))
                .andExpect(jsonPath("$.staffDto").exists())
                .andExpect(jsonPath("$.departmentDto").exists());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_003: Get doctor by non-existent ID should return 404")
    void whenGetDoctorByNonExistentId_thenNotFound() throws Exception {
        // Given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ==================== BACKEND_DOCTOR_004 ====================
    @Test
    @DisplayName("BACKEND_DOCTOR_004: Get doctor's patients list")
    void whenGetDoctorPatients_thenReturnPatientsList() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}/patients", testDoctor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_004: Get patients for non-existent doctor")
    void whenGetPatientsForNonExistentDoctor_thenReturnEmptyList() throws Exception {
        // Given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}/patients", nonExistentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== BACKEND_DOCTOR_005 ====================
    @Test
    @DisplayName("BACKEND_DOCTOR_005: Get doctor's shift schedule for specific date")
    void whenGetDoctorShiftSchedule_thenReturnSchedule() throws Exception {
        // Given
        LocalDate testDate = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}/schedule", testDoctor.getId())
                        .param("date", testDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_005: Get schedule without date parameter should fail")
    void whenGetScheduleWithoutDateParam_thenBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}/schedule", testDoctor.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("BACKEND_DOCTOR_005: Get schedule for non-existent doctor")
    void whenGetScheduleForNonExistentDoctor_thenReturnEmptyList() throws Exception {
        // Given
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        LocalDate testDate = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/doctors/{doctorId}/schedule", nonExistentId)
                        .param("date", testDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}