package com.pulseclinic.pulse_server.modules.rooms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomRequestDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
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

import com.pulseclinic.pulse_server.mappers.impl.RoomMapper;

import java.time.LocalDateTime;

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
@DisplayName("Room Integration Tests")
public class RoomIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoomMapper roomMapper;

    private Department testDepartment;
    private Department testDepartment2;
    private Department testDepartment3;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        // Clean up before each test
//        roomRepository.deleteAll();
//        departmentRepository.deleteAll();

        entityManager.createNativeQuery("DELETE FROM doctors").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM staff").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM rooms").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM departments").executeUpdate();


        // Create test departments
        testDepartment = Department.builder()
                .createdAt(LocalDateTime.now())
                .name("Cardiology")
                .description("Heart department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testDepartment2 = Department.builder()
                .name("Neurology")
                .createdAt(LocalDateTime.now())
                .description("Brain department")
                .build();
        testDepartment2 = departmentRepository.save(testDepartment2);

        testDepartment3 = Department.builder()
                .name("Orthopedics")
                .description("Bone and joint department")
                .createdAt(LocalDateTime.now())
                .build();
        testDepartment3 = departmentRepository.save(testDepartment3);

        // Create test room
        testRoom = Room.builder()
                .roomNumber("B101")
                .bedAmount(2)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .department(testDepartment)
                .build();
        testRoom = roomRepository.save(testRoom);
    }

    // ========================================
    // BACKEND_ROOM_001: Create new room
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_001: Should create new room successfully")
    void testCreateRoom_Success() throws Exception {
        // Given
        RoomRequestDto requestDto = RoomRequestDto.builder()
                .roomNumber("B102")
                .bedAmount(4)
                .isAvailable(true)
                .departmentId(testDepartment.getId())
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.roomNumber").value("B102"))
                .andExpect(jsonPath("$.bedAmount").value(4))
                .andExpect(jsonPath("$.isAvailable").value(true))
//                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        // Verify in database
        String responseBody = result.getResponse().getContentAsString();
        RoomDto createdRoom = objectMapper.readValue(responseBody, RoomDto.class);

        Room savedRoom = roomRepository.findById(createdRoom.getId()).orElse(null);
        assertThat(savedRoom).isNotNull();
        assertThat(savedRoom.getRoomNumber()).isEqualTo("B102");
        assertThat(savedRoom.getBedAmount()).isEqualTo(4);
        assertThat(savedRoom.getDepartment().getId()).isEqualTo(testDepartment.getId());
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_001: Should create room linked to department")
    void testCreateRoom_LinkedToDepartment() throws Exception {
        // Given
        RoomRequestDto requestDto = RoomRequestDto.builder()
                .roomNumber("B103")
                .bedAmount(3)
                .isAvailable(true)
                .departmentId(testDepartment2.getId())
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // Verify department linkage
        String responseBody = result.getResponse().getContentAsString();
        RoomDto createdRoom = objectMapper.readValue(responseBody, RoomDto.class);

        Room savedRoom = roomRepository.findById(createdRoom.getId()).orElseThrow();
        assertThat(savedRoom.getDepartment()).isNotNull();
        assertThat(savedRoom.getDepartment().getId()).isEqualTo(testDepartment2.getId());
        assertThat(savedRoom.getDepartment().getName()).isEqualTo("Neurology");
    }

    // ========================================
    // BACKEND_ROOM_002: Get all rooms
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_002: Should retrieve all rooms successfully")
    void testGetAllRooms_Success() throws Exception {
        // Given - Create additional rooms
        Room room2 = Room.builder()
                .roomNumber("B201")
                .bedAmount(1)
                .isAvailable(false)
                .department(testDepartment2)
                .build();
        roomRepository.save(room2);

        Room room3 = Room.builder()
                .roomNumber("B202")
                .bedAmount(3)
                .isAvailable(true)
                .department(testDepartment)
                .build();
        roomRepository.save(room3);

        // When & Then
        mockMvc.perform(get("/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("B101", "B201", "B202")))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].bedAmount").exists())
                .andExpect(jsonPath("$[0].isAvailable").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_ROOM_002: Staff should also retrieve all rooms")
    void testGetAllRooms_AsStaff_Success() throws Exception {
        // When & Then - Staff authority should work
        mockMvc.perform(get("/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_002: Should not return deleted rooms")
    void testGetAllRooms_ExcludeDeleted() throws Exception {
        // Given - Mark one room as deleted
        testRoom.setDeletedAt(java.time.LocalDateTime.now());
//        testRoom.set
        roomRepository.save(testRoom);

        // Create a non-deleted room
        Room activeRoom = Room.builder()
                .roomNumber("B203")
                .bedAmount(2)
                .isAvailable(true)
                .department(testDepartment)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        roomRepository.save(activeRoom);

        // When & Then - Should only return non-deleted rooms
        mockMvc.perform(get("/rooms")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomNumber").value("B203"));
    }

    // ========================================
    // BACKEND_ROOM_003: Get rooms by department
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_003: Should retrieve rooms filtered by department")
    void testGetRoomsByDepartment_Success() throws Exception {
        // Given - Create rooms in different departments
        Room room2 = Room.builder()
                .roomNumber("B201")
                .bedAmount(1)
                .isAvailable(true)
                .department(testDepartment) // Same department as testRoom
                .build();
        roomRepository.save(room2);

        Room room3 = Room.builder()
                .roomNumber("B301")
                .bedAmount(2)
                .isAvailable(true)
                .department(testDepartment2) // Different department
                .build();
        roomRepository.save(room3);

        // When & Then - Should only return rooms from testDepartment
        mockMvc.perform(get("/rooms/by-department")
                        .param("departmentId", testDepartment.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("B101", "B201")));
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_ROOM_003: Staff should also retrieve rooms by department")
    void testGetRoomsByDepartment_AsStaff_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/rooms/by-department")
                        .param("departmentId", testDepartment.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_003: Should return empty array when department has no rooms")
    void testGetRoomsByDepartment_EmptyList() throws Exception {
        // Given - Create a department with no rooms
        Department emptyDept = Department.builder()
                .name("Radiology")
                .description("Imaging department")
                .build();
        emptyDept = departmentRepository.save(emptyDept);

        // When & Then
        mockMvc.perform(get("/rooms/by-department")
                        .param("departmentId", emptyDept.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========================================
    // BACKEND_ROOM_004: Update room availability status
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_004: Should toggle room availability status successfully")
    void testUpdateRoomStatus_Success() throws Exception {
        // Given - Room is initially available (true)
        assertThat(testRoom.getIsAvailable()).isTrue();

        // When & Then - First toggle (true -> false)
        mockMvc.perform(patch("/rooms/status/{id}", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));

        // Verify in database
        Room updatedRoom = roomRepository.findById(testRoom.getId()).orElseThrow();
        assertThat(updatedRoom.getIsAvailable()).isFalse();

        // When & Then - Second toggle (false -> true)
        mockMvc.perform(patch("/rooms/status/{id}", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(true));

        // Verify in database again
        updatedRoom = roomRepository.findById(testRoom.getId()).orElseThrow();
        assertThat(updatedRoom.getIsAvailable()).isTrue();
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_ROOM_004: Staff should also update room status")
    void testUpdateRoomStatus_AsStaff_Success() throws Exception {
        // When & Then
        mockMvc.perform(patch("/rooms/status/{id}", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }

    // ========================================
    // BACKEND_ROOM_005: Update room details
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_005: Should update room details successfully")
    void testUpdateRoomDetails_Success() throws Exception {
        // Given
        RoomDto updateDto = RoomDto.builder()
                .roomNumber("B101-Updated")
                .bedAmount(5)
                .isAvailable(false)
                .build();

        // When & Then
        mockMvc.perform(patch("/rooms/{id}", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("B101-Updated"))
                .andExpect(jsonPath("$.bedAmount").value(5))
                .andExpect(jsonPath("$.isAvailable").value(false));

        // Verify in database
        Room updatedRoom = roomRepository.findById(testRoom.getId()).orElseThrow();
        assertThat(updatedRoom.getRoomNumber()).isEqualTo("B101-Updated");
        assertThat(updatedRoom.getBedAmount()).isEqualTo(5);
        assertThat(updatedRoom.getIsAvailable()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_ROOM_005: Should partially update room details")
    void testUpdateRoomDetails_PartialUpdate() throws Exception {
        // Given - Only update bedAmount
        RoomDto updateDto = RoomDto.builder()
                .bedAmount(10)
                .build();

        // When & Then
        mockMvc.perform(patch("/rooms/{id}", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("B101")) // Original
                .andExpect(jsonPath("$.bedAmount").value(10)) // Updated
                .andExpect(jsonPath("$.isAvailable").value(true)); // Original
    }
}