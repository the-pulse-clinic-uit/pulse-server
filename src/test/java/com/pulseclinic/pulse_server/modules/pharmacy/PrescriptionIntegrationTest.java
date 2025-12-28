package com.pulseclinic.pulse_server.modules.pharmacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.*;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription.PrescriptionRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Prescription;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.PrescriptionDetail;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionDetailRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.PrescriptionRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Prescription Integration Tests")
public class PrescriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    @Autowired
    private EncounterRepository encounterRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private EntityManager entityManager;

    private Encounter testEncounter;
    private Drug testDrug1;
    private Drug testDrug2;
    private Patient testPatient;
    private Doctor testDoctor;

    private static Server h2TcpServer;

    @BeforeAll
    static void startServer() throws SQLException {
        h2TcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
    }

    @AfterAll
    static void stopServer() {
        if (h2TcpServer != null) {
            h2TcpServer.stop();
        }
    }


    @BeforeEach
    void setUp() {
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

        // Create test patient
        User patientUser = User.builder()
                .email("patient@test.com")
                .hashedPassword("password")
                .fullName("John Patient")
                .citizenId("123456789")
                .phone("0123456789")
                .gender(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .build();

        patientUser = userRepository.save(patientUser);

        testPatient = Patient.builder()
                .healthInsuranceId("HI123456")
                .bloodType(BloodType.O)
                .allergies("None")
                .user(patientUser)
                .build();
        testPatient = patientRepository.save(testPatient);

        // Create test doctor
        User doctorUser = User.builder()
                .email("doctor@test.com")
                .hashedPassword("password")
                .fullName("Dr. Smith")
                .citizenId("987654321")
                .phone("0987654321")
                .gender(true)
                .birthDate(LocalDate.of(1980, 1, 1))
                .isActive(true)
                .build();

        doctorUser = userRepository.save(doctorUser);

        Staff doctorStaff = Staff.builder()
                .position(Position.STAFF)
                .user(doctorUser)
                .build();
        doctorStaff = staffRepository.save(doctorStaff);

        testDoctor = Doctor.builder()
                .licenseId("DOC12345")
                .isVerified(true)
                .staff(doctorStaff)
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        // Create test encounter
        testEncounter = Encounter.builder()
                .type(EncounterType.APPOINTED)
                .diagnosis("Common cold")
                .notes("Patient has mild symptoms")
                .patient(testPatient)
                .createdAt(LocalDateTime.now())
                .doctor(testDoctor)
                .build();
        testEncounter = encounterRepository.save(testEncounter);

        // Create test drugs
        testDrug1 = Drug.builder()
                .name("Paracetamol")
                .dosageForm(DrugDosageForm.TABLET)
                .unit(DrugUnit.TABLET)
                .strength("500mg")
                .unitPrice(new BigDecimal("2.50"))
                .build();
        testDrug1 = drugRepository.save(testDrug1);

        testDrug2 = Drug.builder()
                .name("Amoxicillin")
                .dosageForm(DrugDosageForm.CAPSULE)
                .unit(DrugUnit.CAPSULE)
                .strength("250mg")
                .unitPrice(new BigDecimal("5.00"))
                .build();
        testDrug2 = drugRepository.save(testDrug2);
    }

    // ========================================
    // BACKEND_RX_001: Create new prescription from encounter
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_001: Should create new prescription from encounter successfully")
    void testCreatePrescription_Success() throws Exception {
        // Given
        PrescriptionRequestDto requestDto = PrescriptionRequestDto.builder()
                .notes("Take medication as prescribed")
                .encounterId(testEncounter.getId())
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.notes").value("Take medication as prescribed"))
                .andExpect(jsonPath("$.totalPrice").value(0.0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        // Verify in database
        String responseBody = result.getResponse().getContentAsString();
        PrescriptionDto createdPrescription = objectMapper.readValue(responseBody, PrescriptionDto.class);

        Prescription savedPrescription = prescriptionRepository.findById(createdPrescription.getId()).orElseThrow();
        assertThat(savedPrescription.getStatus()).isEqualTo(PrescriptionStatus.DRAFT);
        assertThat(savedPrescription.getEncounter().getId()).isEqualTo(testEncounter.getId());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_RX_001: Staff should also create prescription")
    void testCreatePrescription_AsStaff_Success() throws Exception {
        // Given
        PrescriptionRequestDto requestDto = PrescriptionRequestDto.builder()
                .notes("Staff created prescription")
                .encounterId(testEncounter.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_001: Should fail when encounter not found")
    void testCreatePrescription_EncounterNotFound() throws Exception {
        // Given
        UUID nonExistentEncounterId = UUID.randomUUID();
        PrescriptionRequestDto requestDto = PrescriptionRequestDto.builder()
                .notes("Invalid encounter")
                .encounterId(nonExistentEncounterId)
                .build();

        // When & Then
        mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // BACKEND_RX_002: Get prescription details
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_002: Should retrieve prescription details with subtotals")
    void testGetPrescriptionDetails_Success() throws Exception {
        // Given - Create prescription with drug items
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test prescription")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // Add drug items
        PrescriptionDetail detail1 = PrescriptionDetail.builder()
                .drug(testDrug1)
                .prescription(prescription)
                .quantity(10)
                .unitPrice(new BigDecimal("2.50"))
                .itemTotalPrice(new BigDecimal("25.00"))
                .dose("1 tablet")
                .frequency("3 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();
        prescriptionDetailRepository.save(detail1);

        PrescriptionDetail detail2 = PrescriptionDetail.builder()
                .drug(testDrug2)
                .prescription(prescription)
                .quantity(6)
                .unitPrice(new BigDecimal("5.00"))
                .itemTotalPrice(new BigDecimal("30.00"))
                .dose("1 capsule")
                .frequency("2 times per day")
                .timing("Before meal")
                .instructions("Complete the course")
                .build();
        prescriptionDetailRepository.save(detail2);

        // When & Then
        mockMvc.perform(get("/prescriptions/{id}/details", prescription.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].quantity").exists())
                .andExpect(jsonPath("$[0].unitPrice").exists())
                .andExpect(jsonPath("$[0].itemTotalPrice").exists())
                .andExpect(jsonPath("$[0].dose").exists())
                .andExpect(jsonPath("$[0].frequency").exists())
                .andExpect(jsonPath("$[0].timing").exists());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_RX_002: Staff should also retrieve prescription details")
    void testGetPrescriptionDetails_AsStaff_Success() throws Exception {
        // Given
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then
        mockMvc.perform(get("/prescriptions/{id}/details", prescription.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_002: Should return empty array when no drug items")
    void testGetPrescriptionDetails_EmptyList() throws Exception {
        // Given - Prescription without drug items
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Empty prescription")
                .createdAt(LocalDateTime.now())
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then
        mockMvc.perform(get("/prescriptions/{id}/details", prescription.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
//                        .with(jwt().authr)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========================================
    // BACKEND_RX_003: Finalize prescription
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_003: Should finalize prescription from DRAFT to FINAL")
    void testFinalizePrescription_Success() throws Exception {
        // Given - Create prescription in DRAFT status with items
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test prescription")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        PrescriptionDetail detail = PrescriptionDetail.builder()
                .drug(testDrug1)
                .prescription(prescription)
                .quantity(10)
                .unitPrice(new BigDecimal("2.50"))
                .itemTotalPrice(new BigDecimal("25.00"))
                .dose("1 tablet")
                .frequency("3 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();
        prescriptionDetailRepository.save(detail);

        // When & Then
        mockMvc.perform(put("/prescriptions/{id}/finalize", prescription.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify status changed to FINAL
        Prescription finalizedPrescription = prescriptionRepository.findById(prescription.getId()).orElseThrow();
        assertThat(finalizedPrescription.getStatus()).isEqualTo(PrescriptionStatus.FINAL);
        assertThat(finalizedPrescription.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_003: Should lock editing after finalization")
    void testFinalizePrescription_EditingLocked() throws Exception {
        // Given - Finalized prescription
        Prescription prescription = Prescription.builder()
                .totalPrice(new BigDecimal("25.00"))
                .notes("Test prescription")
                .status(PrescriptionStatus.FINAL)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then - Try to finalize again (should fail)
        mockMvc.perform(put("/prescriptions/{id}/finalize", prescription.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_RX_003: Staff should also finalize prescription")
    void testFinalizePrescription_AsStaff_Success() throws Exception {
        // Given
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then
        mockMvc.perform(put("/prescriptions/{prescriptionId}/finalize", prescription.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // ========================================
    // BACKEND_RX_004: Dispense prescription
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_004: Should dispense prescription from FINAL to DISPENSED")
    void testDispensePrescription_Success() throws Exception {
        // Given - Prescription in FINAL status
        Prescription prescription = Prescription.builder()
                .totalPrice(new BigDecimal("25.00"))
                .notes("Test prescription")
                .status(PrescriptionStatus.FINAL)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then
        mockMvc.perform(put("/prescriptions/{id}/dispense", prescription.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify status changed to DISPENSED
        Prescription dispensedPrescription = prescriptionRepository.findById(prescription.getId()).orElseThrow();
        assertThat(dispensedPrescription.getStatus()).isEqualTo(PrescriptionStatus.DISPENSED);
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_004: Should fail when prescription not in FINAL status")
    void testDispensePrescription_NotFinalStatus() throws Exception {
        // Given - Prescription in DRAFT status
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test prescription")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then - Cannot dispense DRAFT prescription
        mockMvc.perform(put("/prescriptions/{id}/dispense", prescription.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "staff")
    @DisplayName("BACKEND_RX_004: Staff should also dispense prescription")
    void testDispensePrescription_AsStaff_Success() throws Exception {
        // Given
        Prescription prescription = Prescription.builder()
                .totalPrice(new BigDecimal("25.00"))
                .notes("Test")
                .status(PrescriptionStatus.FINAL)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        // When & Then
        mockMvc.perform(put("/prescriptions/{id}/dispense", prescription.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // ========================================
    // BACKEND_RX_005: Add drug item to prescription
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_005: Should add drug item to prescription successfully")
    void testAddDrugItem_Success() throws Exception {
        // Given - Prescription in DRAFT status
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test prescription")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        PrescriptionDetailRequestDto detailRequest = PrescriptionDetailRequestDto.builder()
                .drugId(testDrug1.getId())
                .prescriptionId(prescription.getId())
                .quantity(10)
                .unitPrice(new BigDecimal("2.50"))
                .itemTotalPrice(new BigDecimal("25.00"))
                .dose("1 tablet")
                .frequency("3 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/prescriptions/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.unitPrice").value(2.50))
                .andExpect(jsonPath("$.itemTotalPrice").value(25.00))
                .andReturn();

        // Verify item linked to prescription
        String responseBody = result.getResponse().getContentAsString();
        PrescriptionDetailDto createdDetail = objectMapper.readValue(responseBody, PrescriptionDetailDto.class);

        PrescriptionDetail savedDetail = prescriptionDetailRepository.findById(createdDetail.getId()).orElseThrow();
        assertThat(savedDetail.getPrescription().getId()).isEqualTo(prescription.getId());
        assertThat(savedDetail.getDrug().getId()).isEqualTo(testDrug1.getId());
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_005: Should use drug's default price if not provided")
    void testAddDrugItem_DefaultPrice() throws Exception {
        // Given
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        PrescriptionDetailRequestDto detailRequest = PrescriptionDetailRequestDto.builder()
                .drugId(testDrug1.getId())
                .prescriptionId(prescription.getId())
                .quantity(5)
                .dose("1 tablet")
                .frequency("2 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();

        // When & Then - Should use drug's unitPrice (2.50)
        mockMvc.perform(post("/prescriptions/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unitPrice").value(2.50))
                .andExpect(jsonPath("$.itemTotalPrice").value(12.50)); // 5 * 2.50
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_005: Should fail when prescription not found")
    void testAddDrugItem_PrescriptionNotFound() throws Exception {
        // Given
        UUID nonExistentPrescriptionId = UUID.randomUUID();
        PrescriptionDetailRequestDto detailRequest = PrescriptionDetailRequestDto.builder()
                .drugId(testDrug1.getId())
                .prescriptionId(nonExistentPrescriptionId)
                .quantity(10)
                .dose("1 tablet")
                .frequency("3 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();

        // When & Then
        mockMvc.perform(post("/prescriptions/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_RX_005: Should fail when drug not found")
    void testAddDrugItem_DrugNotFound() throws Exception {
        // Given
        Prescription prescription = Prescription.builder()
                .totalPrice(BigDecimal.ZERO)
                .notes("Test")
                .status(PrescriptionStatus.DRAFT)
                .encounter(testEncounter)
                .build();
        prescription = prescriptionRepository.save(prescription);

        UUID nonExistentDrugId = UUID.randomUUID();
        PrescriptionDetailRequestDto detailRequest = PrescriptionDetailRequestDto.builder()
                .drugId(nonExistentDrugId)
                .prescriptionId(prescription.getId())
                .quantity(10)
                .dose("1 tablet")
                .frequency("3 times per day")
                .timing("After meal")
                .instructions("Take with water")
                .build();

        // When & Then
        mockMvc.perform(post("/prescriptions/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}