package com.pulseclinic.pulse_server.modules.scheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.enums.ShiftKind;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftAssignmentRepository;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftRepository;
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
import java.util.ArrayList;
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
class ShiftIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoomRepository roomRepository;

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

    // Test data
    private Role doctorRole;
    private Role staffRole;
    private Role patientRole;

    private User doctorUser;
    private User staffUser;
    private User patientUser;

    private Department cardiology;
    private Department emergency;

    private Room room101;
    private Room room102;

    private Staff doctorStaff;
    private Doctor testDoctor;

    private Shift morningShift;
    private Shift afternoonShift;

    private String doctorToken;
    private String staffToken;
    private String patientToken;

    @BeforeEach
    void setUp() {
        // clean database with truncate cascade (postgresql compatible)
        entityManager.createNativeQuery("TRUNCATE TABLE shift_assignments, shifts, doctors, staff, rooms, departments, users, roles RESTART IDENTITY CASCADE").executeUpdate();

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
                .createdAt(LocalDateTime.now())
                .staff(new ArrayList<>())
                .build();

        emergency = Department.builder()
                .name("Emergency")
                .description("Emergency department")
                .staff(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        cardiology = departmentRepository.save(cardiology);
        emergency = departmentRepository.save(emergency);

        // Create rooms
        room101 = Room.builder()
                .roomNumber("R101")
                .bedAmount(1)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .department(cardiology)
                .build();

        room102 = Room.builder()
                .roomNumber("R102")
                .bedAmount(2)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .department(emergency)
                .build();

        room101 = roomRepository.save(room101);
        room102 = roomRepository.save(room102);

        // Create users
        doctorUser = User.builder()
                .email("doctor@example.com")
                .fullName("Dr. John Smith")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(doctorRole)
                .phone("0123456789")
                .citizenId("123456789012")
                .birthDate(LocalDate.of(1985, 5, 15))
                .gender(true)
                .address("123 Doctor Street")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        staffUser = User.builder()
                .email("staff@example.com")
                .fullName("Staff Member")
                .hashedPassword(passwordEncoder.encode("Password123!"))
                .role(staffRole)
                .phone("0123456790")
                .citizenId("123456789013")
                .birthDate(LocalDate.of(1990, 3, 20))
                .gender(false)
                .address("456 Staff Street")
                .isActive(true)
                .createdAt(LocalDateTime.now())
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
                .address("789 Patient Street")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        doctorUser = userRepository.save(doctorUser);
        staffUser = userRepository.save(staffUser);
        patientUser = userRepository.save(patientUser);

        // Create staff and doctor
        doctorStaff = Staff.builder()
                .user(doctorUser)
                .position(Position.DOCTOR)
                .department(cardiology)
                .build();
        doctorStaff = staffRepository.save(doctorStaff);

        testDoctor = Doctor.builder()
                .licenseId("LIC-DOC-001")
                .isVerified(true)
                .staff(doctorStaff)
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        // Create test shifts
        morningShift = Shift.builder()
                .name("Morning Shift")
                .kind(ShiftKind.CLINIC)
                .startTime(LocalDateTime.of(2025, 1, 15, 8, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 12, 0))
                .slotMinutes(30)
                .capacityPerSlot(2)
                .department(cardiology)
                .defaultRoom(room101)
                .build();
        morningShift = shiftRepository.save(morningShift);

        afternoonShift = Shift.builder()
                .name("Afternoon Shift")
                .kind(ShiftKind.CLINIC)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 17, 0))
                .slotMinutes(30)
                .capacityPerSlot(1)
                .department(cardiology)
                .defaultRoom(room101)
                .build();
        afternoonShift = shiftRepository.save(afternoonShift);

        // Generate JWT tokens
        doctorToken = jwtService.generateToken(doctorUser);
        staffToken = jwtService.generateToken(staffUser);
        patientToken = jwtService.generateToken(patientUser);
    }

    // ==================== BACKEND_SHIFT_001 ====================
    @Test
    @DisplayName("BACKEND_SHIFT_001: Create new shift with doctor role")
    void whenCreateShiftWithDoctorRole_thenShiftCreated() throws Exception {
        // Given
        ShiftRequestDto request = ShiftRequestDto.builder()
                .name("Evening Shift")
                .kind(ShiftKind.ER)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(15))
                .slotMinutes(0)
                .capacityPerSlot(3)
                .departmentId(emergency.getId())
                .defaultRoomId(room102.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/shifts")
//                        .header("Authorization", "Bearer " + doctorToken)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Evening Shift"))
                .andExpect(jsonPath("$.kind").value("ER"))
                .andExpect(jsonPath("$.slotMinutes").value(0))
                .andExpect(jsonPath("$.capacityPerSlot").value(3))
                .andExpect(jsonPath("$.departmentDto").exists())
                .andExpect(jsonPath("$.defaultRoomDto").exists());

        // Verify shift created in database
        Optional<Shift> savedShift = shiftRepository.findByName("Evening Shift");
        assertThat(savedShift).isPresent();
        assertThat(savedShift.get().getKind()).isEqualTo(ShiftKind.ER);
        assertThat(savedShift.get().getSlotMinutes()).isEqualTo(0);
        assertThat(savedShift.get().getCapacityPerSlot()).isEqualTo(3);
    }

    // ==================== BACKEND_SHIFT_002 ====================
    @Test
    @DisplayName("BACKEND_SHIFT_002: Get all shifts with doctor role")
    void whenGetAllShiftsWithDoctorRole_thenReturnAllShifts() throws Exception {
        // When & Then
        mockMvc.perform(get("/shifts")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].slotMinutes").exists())
                .andExpect(jsonPath("$[0].capacityPerSlot").exists());
    }

    @Test
    @DisplayName("BACKEND_SHIFT_002: Get all shifts with staff role")
    void whenGetAllShiftsWithStaffRole_thenReturnAllShifts() throws Exception {
        // When & Then
        mockMvc.perform(get("/shifts")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("BACKEND_SHIFT_002: Get shift by ID")
    void whenGetShiftById_thenReturnShift() throws Exception {
        // When & Then
        mockMvc.perform(get("/shifts/{shiftId}", morningShift.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(morningShift.getId().toString()))
                .andExpect(jsonPath("$.name").value("Morning Shift"))
                .andExpect(jsonPath("$.kind").value("CLINIC"))
                .andExpect(jsonPath("$.slotMinutes").value(30))
                .andExpect(jsonPath("$.capacityPerSlot").value(2));
    }

    // ==================== BACKEND_SHIFT_003 ====================
    @Test
    @DisplayName("BACKEND_SHIFT_003: Assign doctor to shift with doctor role")
    void whenAssignDoctorToShiftWithDoctorRole_thenAssignmentCreated() throws Exception {
        // Given
        LocalDate dutyDate = LocalDate.now();
        ShiftAssignmentRequestDto request = ShiftAssignmentRequestDto.builder()
                .doctorId(testDoctor.getId())
                .shiftId(morningShift.getId())
                .dutyDate(dutyDate)
                .roleInShift(ShiftAssignmentRole.PRIMARY)
                .status(ShiftAssignmentStatus.ACTIVE)
                .roomId(room101.getId())
                .notes("Regular morning duty")
                .build();

        // When & Then
        mockMvc.perform(post("/shifts/assignments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dutyDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.roleInShift").value("PRIMARY"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.doctorDto").exists())
                .andExpect(jsonPath("$.shiftDto").exists())
                .andExpect(jsonPath("$.roomDto").exists())
                .andExpect(jsonPath("$.notes").value("Regular morning duty"));

        // Verify assignment created in database
        Optional<ShiftAssignment> savedAssignment = shiftAssignmentRepository.findAll().stream()
                .filter(sa -> sa.getDutyDate().equals(dutyDate) &&
                        sa.getDoctor().getId().equals(testDoctor.getId()))
                .findFirst();

        assertThat(savedAssignment).isPresent();
        assertThat(savedAssignment.get().getRoleInShift()).isEqualTo(ShiftAssignmentRole.PRIMARY);
        assertThat(savedAssignment.get().getStatus()).isEqualTo(ShiftAssignmentStatus.ACTIVE);
        assertThat(savedAssignment.get().getRoom().getId()).isEqualTo(room101.getId());
    }

    @Test
    @DisplayName("BACKEND_SHIFT_003: Assign doctor with ON_CALL role")
    void whenAssignDoctorWithOnCallRole_thenAssignmentCreated() throws Exception {
        // Given
        ShiftAssignmentRequestDto request = ShiftAssignmentRequestDto.builder()
                .doctorId(testDoctor.getId())
                .shiftId(afternoonShift.getId())
                .dutyDate(LocalDate.now())
                .roleInShift(ShiftAssignmentRole.ON_CALL)
                .status(ShiftAssignmentStatus.ACTIVE)
                .build();

        // When & Then
        mockMvc.perform(post("/shifts/assignments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleInShift").value("ON_CALL"));
    }

    // ==================== BACKEND_SHIFT_004 ====================
    @Test
    @DisplayName("BACKEND_SHIFT_004: Get shift assignments for specific date")
    void whenGetShiftAssignmentsForDate_thenReturnFilteredAssignments() throws Exception {
        // Given - Create assignment for specific date
        LocalDate testDate = LocalDate.of(2025, 1, 25);
        ShiftAssignment assignment = ShiftAssignment.builder()
                .doctor(testDoctor)
                .shift(morningShift)
                .dutyDate(testDate)
                .roleInShift(ShiftAssignmentRole.PRIMARY)
                .status(ShiftAssignmentStatus.ACTIVE)
                .room(room101)
                .build();
        shiftAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(get("/shifts/{shiftId}/assignments", morningShift.getId())
                        .param("date", testDate.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].dutyDate").value(testDate.toString()))
                .andExpect(jsonPath("$[0].shiftDto.id").value(morningShift.getId().toString()))
                .andExpect(jsonPath("$[0].doctorDto").exists());
    }

    @Test
    @DisplayName("BACKEND_SHIFT_004: Get assignments for date with no assignments returns empty array")
    void whenGetAssignmentsForDateWithNoData_thenReturnEmptyArray() throws Exception {
        // Given - Date with no assignments
        LocalDate futureDate = LocalDate.now().plusDays(15);

        // When & Then
        mockMvc.perform(get("/shifts/{shiftId}/assignments", morningShift.getId())
                        .param("date", futureDate.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== BACKEND_SHIFT_005 ====================
    @Test
    @DisplayName("BACKEND_SHIFT_005: Update shift assignment status to CANCELLED")
    void whenUpdateAssignmentStatusToCancelled_thenStatusUpdated() throws Exception {
        // Given - Create assignment
        ShiftAssignment assignment = ShiftAssignment.builder()
                .doctor(testDoctor)
                .shift(morningShift)
                .dutyDate(LocalDate.of(2025, 1, 26))
                .roleInShift(ShiftAssignmentRole.PRIMARY)
                .status(ShiftAssignmentStatus.ACTIVE)
                .build();
        assignment = shiftAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(put("/shifts/assignments/{assignmentId}/status", assignment.getId())
                        .param("status", "CANCELLED")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify status updated in database
        ShiftAssignment updatedAssignment = shiftAssignmentRepository
                .findById(assignment.getId()).orElseThrow();
        assertThat(updatedAssignment.getStatus()).isEqualTo(ShiftAssignmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("BACKEND_SHIFT_005: Update assignment status to ACTIVE")
    void whenUpdateAssignmentStatusToActive_thenStatusUpdated() throws Exception {
        // Given - Create cancelled assignment
        ShiftAssignment assignment = ShiftAssignment.builder()
                .doctor(testDoctor)
                .shift(afternoonShift)
                .dutyDate(LocalDate.of(2025, 1, 27))
                .roleInShift(ShiftAssignmentRole.ON_CALL)
                .status(ShiftAssignmentStatus.CANCELLED)
                .build();
        assignment = shiftAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(put("/shifts/assignments/{assignmentId}/status", assignment.getId())
                        .param("status", "ACTIVE")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor"))))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify status updated
        ShiftAssignment updatedAssignment = shiftAssignmentRepository
                .findById(assignment.getId()).orElseThrow();
        assertThat(updatedAssignment.getStatus()).isEqualTo(ShiftAssignmentStatus.ACTIVE);
    }
}