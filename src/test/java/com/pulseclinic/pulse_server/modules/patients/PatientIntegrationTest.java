package com.pulseclinic.pulse_server.modules.patients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.BloodType;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientSearchDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
class PatientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Test data
    private Role patientRole;
    private Role doctorRole;
    private Role staffRole;

    private User patientUser;
    private User doctorUser;
    private User staffUser;
    private User newPatientUser;

    private Patient testPatient;

    private String patientToken;
    private String doctorToken;
    private String staffToken;

    @BeforeEach
    void setUp() {
        // Clean database
        patientRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        patientRole = Role.builder().name("PATIENT").build();
        doctorRole = Role.builder().name("DOCTOR").build();
        staffRole = Role.builder().name("STAFF").build();

        patientRole = roleRepository.save(patientRole);
        doctorRole = roleRepository.save(doctorRole);
        staffRole = roleRepository.save(staffRole);

        // Create users with different roles
        patientUser = User.builder()
                .email("patient@example.com")
                .fullName("John Patient")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(patientRole)
                .phone("0123456789")
                .citizenId("123456789012")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(true) // Male
                .address("123 Patient Street")
                .isActive(true)
                .build();

        doctorUser = User.builder()
                .email("doctor@example.com")
                .fullName("Dr. Smith")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0123456790")
                .citizenId("123456789013")
                .birthDate(LocalDate.of(1985, 3, 20))
                .gender(true)
                .address("456 Doctor Street")
                .isActive(true)
                .build();

        staffUser = User.builder()
                .email("staff@example.com")
                .fullName("Staff Member")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(staffRole)
                .phone("0123456791")
                .citizenId("123456789014")
                .birthDate(LocalDate.of(1992, 8, 10))
                .gender(false) // Female
                .address("789 Staff Street")
                .isActive(true)
                .build();

        // User for creating new patient
        newPatientUser = User.builder()
                .email("newpatient@example.com")
                .fullName("New Patient User")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(patientRole)
                .phone("0987654321")
                .citizenId("987654321012")
                .birthDate(LocalDate.of(1995, 7, 25))
                .gender(false)
                .address("321 New Street")
                .isActive(true)
                .build();

        patientUser = userRepository.save(patientUser);
        doctorUser = userRepository.save(doctorUser);
        staffUser = userRepository.save(staffUser);
        newPatientUser = userRepository.save(newPatientUser);

        // Create existing patient
        testPatient = Patient.builder()
                .user(patientUser)
                .healthInsuranceId("INS-123456")
                .bloodType(BloodType.A)
                .allergies("Penicillin, Peanuts")
                .build();
        testPatient = patientRepository.save(testPatient);

        // Generate JWT tokens
        patientToken = jwtService.generateToken(patientUser);
        doctorToken = jwtService.generateToken(doctorUser);
        staffToken = jwtService.generateToken(staffUser);
    }

    // ==================== BACKEND_PATIENT_001 ====================
    @Test
    @DisplayName("BACKEND_PATIENT_001: Register new patient with health insurance - Staff role")
    void whenRegisterPatientWithStaffRole_thenPatientCreated() throws Exception {
        // Given
        PatientRequestDto request = PatientRequestDto.builder()
                .userId(newPatientUser.getId())
                .healthInsuranceId("INS-789012")
                .bloodType(BloodType.O)
                .allergies("None")
                .build();

        // When & Then
        mockMvc.perform(post("/patients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
//                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.healthInsuranceId").value("INS-789012"))
                .andExpect(jsonPath("$.bloodType").value("O"))
                .andExpect(jsonPath("$.allergies").value("None"))
                .andExpect(jsonPath("$.userDto").exists())
                .andExpect(jsonPath("$.userDto.email").value("newpatient@example.com"));

        // Verify patient created in database
        Optional<Patient> savedPatient = patientRepository.findAll().stream()
                .filter(p -> "INS-789012" .equals(p.getHealthInsuranceId()))
                .findFirst();

        assertThat(savedPatient).isPresent();
        assertThat(savedPatient.get().getHealthInsuranceId()).isEqualTo("INS-789012");
        assertThat(savedPatient.get().getUser().getId()).isEqualTo(newPatientUser.getId());
    }

    @Test
    @DisplayName("BACKEND_PATIENT_001: Register new patient with doctor role")
    void whenRegisterPatientWithDoctorRole_thenPatientCreated() throws Exception {
        // Given
        PatientRequestDto request = PatientRequestDto.builder()
                .userId(newPatientUser.getId())
                .healthInsuranceId("INS-555555")
                .bloodType(BloodType.AB)
                .allergies("Latex")
                .build();

        // When & Then
        mockMvc.perform(post("/patients")
//                        .header("Authorization", "Bearer " + doctorToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.healthInsuranceId").value("INS-555555"));
    }

    // ==================== BACKEND_PATIENT_002 ====================
    @Test
    @DisplayName("BACKEND_PATIENT_002: Search patients by citizen ID")
    void whenSearchPatientsByCitizenId_thenReturnMatchingPatients() throws Exception {
        // Given
        PatientSearchDto searchDto = PatientSearchDto.builder()
                .citizenId("123456789012")
                .build();

        // When & Then
        mockMvc.perform(post("/patients/search")
//                        .header("Authorization", "Bearer " + staffToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].healthInsuranceId").value("INS-123456"));
    }

    @Test
    @DisplayName("BACKEND_PATIENT_002: Search patients by full name")
    void whenSearchPatientsByFullName_thenReturnMatchingPatients() throws Exception {
        // Given
        PatientSearchDto searchDto = PatientSearchDto.builder()
                .fullName("John")
                .build();

        // When & Then
        mockMvc.perform(post("/patients/search")
//                        .header("Authorization", "Bearer " + doctorToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
//                .andExpect(jsonPath("$[0].userDto.fullName").value(containsString("John")));
    }

    @Test
    @DisplayName("BACKEND_PATIENT_002: Search patients by email")
    void whenSearchPatientsByEmail_thenReturnMatchingPatients() throws Exception {
        // Given
        PatientSearchDto searchDto = PatientSearchDto.builder()
                .email("patient@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/patients/search")
//                        .header("Authorization", "Bearer " + staffToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
//                .andExpect(jsonPath("$[0].userDto.email").value("patient@example.com"));
        // TODO: Re-define user dto to get patient information specifically
    }

    @Test
    @DisplayName("BACKEND_PATIENT_002: Search patients by patient ID")
    void whenSearchPatientsByPatientId_thenReturnMatchingPatient() throws Exception {
        // Given
        PatientSearchDto searchDto = PatientSearchDto.builder()
                .patientId(testPatient.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/patients/search")
//                        .header("Authorization", "Bearer " + doctorToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testPatient.getId().toString()));
    }

    // ==================== BACKEND_PATIENT_003 ====================
    @Test
    @DisplayName("BACKEND_PATIENT_003: Get all patients with staff role")
    void whenGetAllPatientsWithStaffRole_thenReturnAllPatients() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff"))))
//                        .header("Authorization", "Bearer " + staffToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].healthInsuranceId").exists());
//                .andExpect(jsonPath("$[0].userDto").exists())
//                .andExpect(jsonPath("$[0].userDto.fullName").exists());
    }

    @Test
    @DisplayName("BACKEND_PATIENT_003: Get all patients with doctor role")
    void whenGetAllPatientsWithDoctorRole_thenReturnAllPatients() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
//                        .header("Authorization", "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ==================== BACKEND_PATIENT_004 ====================
    @Test
    @DisplayName("BACKEND_PATIENT_004: Get patient by ID with staff role")
    void whenGetPatientByIdWithStaffRole_thenReturnPatient() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients/{id}", testPatient.getId())
//                        .header("Authorization", "Bearer " + staffToken))
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPatient.getId().toString()))
                .andExpect(jsonPath("$.healthInsuranceId").value("INS-123456"))
                .andExpect(jsonPath("$.bloodType").value("A"))
                .andExpect(jsonPath("$.allergies").value("Penicillin, Peanuts"));
//                .andExpect(jsonPath("$.userDto").exists())
//                .andExpect(jsonPath("$.userDto.fullName").value("John Patient"));
    }

    @Test
    @DisplayName("BACKEND_PATIENT_004: Get patient by ID with doctor role")
    void whenGetPatientByIdWithDoctorRole_thenReturnPatient() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients/{id}", testPatient.getId())
//                        .header("Authorization", "Bearer " + doctorToken))
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPatient.getId().toString()));
    }

    // ==================== BACKEND_PATIENT_005 ====================
    @Test
    @DisplayName("BACKEND_PATIENT_005: Patient updates own profile successfully")
    void whenPatientUpdatesOwnProfile_thenProfileUpdated() throws Exception {
        // Given
        PatientDto updateDto = PatientDto.builder()
                .allergies("Penicillin, Peanuts, Shellfish") // Updated allergies
                .id(testPatient.getId())
                .build();

        // When & Then
        mockMvc.perform(patch("/patients/me")
                        .header("Authorization", "Bearer " + patientToken)
//                        .with(jwt().authorities(new SimpleGrantedAuthority("patient")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allergies").value("Penicillin, Peanuts, Shellfish"))
                .andExpect(jsonPath("$.id").value(testPatient.getId().toString()));

        // Verify in database
        Patient updatedPatient = patientRepository.findById(testPatient.getId()).orElseThrow();
        assertThat(updatedPatient.getAllergies()).isEqualTo("Penicillin, Peanuts, Shellfish");
    }

    @Test
    @DisplayName("BACKEND_PATIENT_005: Get patient's own profile")
    void whenPatientGetsOwnProfile_thenReturnProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients/me")
                        .header("Authorization", "Bearer " + patientToken))
//                .with(jwt().authorities(new SimpleGrantedAuthority("patient"))))
//                                .with(jwt()
//                                        .authorities(new SimpleGrantedAuthority("patient"))
//                                        .jwt(j -> j
//                                                .subject("patient@example.com") // Đây là claim 'sub'
//                                                .claim("email", "patient@example.com") // Thêm claim 'email' cho chắc chắn
//                                        )
//                                )
//                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPatient.getId().toString()))
                .andExpect(jsonPath("$.healthInsuranceId").value("INS-123456"));
//                .andExpect(jsonPath("$.userDto.email").value("patient@example.com"));
    }
}