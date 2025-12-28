package com.pulseclinic.pulse_server.modules.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
@DisplayName("Department Integration Tests - Success Cases Only")
public class DepartmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private EntityManager entityManager;

    private Department testDepartment;
    private Staff testStaff;

    @BeforeEach
    void setUp() {
        // Clean up before each test
//        staffRepository.deleteAll();
//        departmentRepository.deleteAll();

        entityManager.createNativeQuery("DELETE FROM doctors").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM staff").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM rooms").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM departments").executeUpdate();

        // Create test department

        // Create test staff (without department initially)
        testStaff = Staff.builder()
                .position(Position.DOCTOR)
                .build();
        testStaff = staffRepository.save(testStaff);
//
//        List<Staff> staffsFromTestDepartment = new ArrayList<>();
//        staffsFromTestDepartment.add(testStaff);

        testDepartment = Department.builder()
                .name("Cardiology")
                .description("Heart and cardiovascular department")
                .staff(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
        testDepartment = departmentRepository.save(testDepartment);
    }

    // ========================================
    // BACKEND_DEPT_001: Create new department
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_001: Should create new department successfully")
    void testCreateDepartment_Success() throws Exception {
        // Given
        DepartmentRequestDto requestDto = DepartmentRequestDto.builder()
                .name("Neurology")
                .description("Brain and nervous system department")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/departments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Neurology"))
                .andExpect(jsonPath("$.description").value("Brain and nervous system department"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        // Verify in database
        String responseBody = result.getResponse().getContentAsString();
        DepartmentDto createdDept = objectMapper.readValue(responseBody, DepartmentDto.class);

        Department savedDept = departmentRepository.findById(createdDept.getId()).orElse(null);
        assertThat(savedDept).isNotNull();
        assertThat(savedDept.getName()).isEqualTo("Neurology");
    }

    // ========================================
    // BACKEND_DEPT_002: Get all departments
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_002: Should retrieve all departments successfully")
    void testGetAllDepartments_Success() throws Exception {
        // Given - Create additional departments
        Department dept2 = Department.builder()
                .name("Orthopedics")
                .description("Bone and joint department")
                .build();
        departmentRepository.save(dept2);

        Department dept3 = Department.builder()
                .name("Pediatrics")
                .description("Children's health department")
                .build();
        departmentRepository.save(dept3);

        // When & Then
        mockMvc.perform(get("/departments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cardiology", "Orthopedics", "Pediatrics")))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_DEPT_002: Staff should also retrieve all departments")
    void testGetAllDepartments_AsStaff_Success() throws Exception {
        // When & Then - Staff authority should also work
        mockMvc.perform(get("/departments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("BACKEND_DEPT_002: Should not return deleted departments")
    void testGetAllDepartments_ExcludeDeleted() throws Exception {
        // Given - Mark one department as deleted
        testDepartment.setDeletedAt(java.time.LocalDateTime.now());
        departmentRepository.save(testDepartment);

        // Create a non-deleted department
        Department activeDept = Department.builder()
                .name("Emergency")
                .description("Emergency department")
                .staff(new ArrayList<>())
                .build();
        departmentRepository.save(activeDept);

        // When & Then - Should only return non-deleted departments
        mockMvc.perform(get("/departments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Emergency"));
    }

    // ========================================
    // BACKEND_DEPT_003: Assign staff to department
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_003: Should assign staff to department successfully")
    void testAssignStaff_Success() throws Exception {
        // Given
        UUID departmentId = testDepartment.getId();
        UUID staffId = testStaff.getId();

        // When & Then
        mockMvc.perform(post("/departments/{id}/staff", departmentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + staffId + "\""))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify in database
        Staff updatedStaff = staffRepository.findById(staffId).orElseThrow();
        assertThat(updatedStaff.getDepartment()).isNotNull();
        assertThat(updatedStaff.getDepartment().getId()).isEqualTo(departmentId);
    }

    // ========================================
    // BACKEND_DEPT_004: Get all staff in department
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_004: Should retrieve all staff in department")
    void testGetAllStaffInDepartment_Success() throws Exception {
        // Given - Assign staff to department
        testStaff.setDepartment(testDepartment);
        staffRepository.save(testStaff);

        // Create and assign another staff
        Staff staff2 = Staff.builder()
                .department(testDepartment)
                .build();
        staffRepository.save(staff2);

        testDepartment.getStaff().add(testStaff);
        testDepartment.getStaff().add(staff2);

        departmentRepository.save(testDepartment);

        // When & Then
        mockMvc.perform(get("/departments/{id}/staff", testDepartment.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        testStaff.getId().toString(),
                        staff2.getId().toString()
                )));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_004: Should return empty array when no staff in department")
    void testGetAllStaffInDepartment_EmptyList() throws Exception {
        // Given - Department with no staff
        Department emptyDept = Department.builder()
                .name("Radiology")
                .description("Imaging department")
                .staff(new ArrayList<>())
                .build();
        emptyDept = departmentRepository.save(emptyDept);

        // When & Then
        mockMvc.perform(get("/departments/{id}/staff", emptyDept.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========================================
    // BACKEND_DEPT_005: Unassign staff from department
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DEPT_005: Should unassign staff from department successfully")
    void testUnassignStaff_Success() throws Exception {
        // Given - Staff assigned to department
        testStaff.setDepartment(testDepartment);
        staffRepository.save(testStaff);

        UUID departmentId = testDepartment.getId();
        UUID staffId = testStaff.getId();

        // When & Then
        mockMvc.perform(delete("/departments/{id}/staff/{staffId}", departmentId, staffId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify in database
        Staff updatedStaff = staffRepository.findById(staffId).orElseThrow();
        assertThat(updatedStaff.getDepartment()).isNull();
    }
}