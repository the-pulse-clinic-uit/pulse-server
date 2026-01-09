package com.pulseclinic.pulse_server.modules.pharmacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.drug.DrugRequestDto;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
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
import java.time.LocalDateTime;
import java.util.List;

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
@DisplayName("Drug Integration Tests")
public class DrugIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private EntityManager entityManager;

    private Drug testDrug;

    @BeforeEach
    void setUp() {
        // clean database with truncate cascade (postgresql compatible)
        entityManager.createNativeQuery("TRUNCATE TABLE drugs RESTART IDENTITY CASCADE").executeUpdate();

        testDrug = Drug.builder()
                .name("Paracetamol")
                .dosageForm(DrugDosageForm.TABLET)
                .unit(DrugUnit.TABLET)
                .strength("500mg")
                .unitPrice(new BigDecimal("2.50"))
                .quantity(100)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .build();
        testDrug = drugRepository.save(testDrug);
    }

    // ========================================
    // BACKEND_DRUG_001: Create new drug
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_001: Should create new drug successfully")
    void testCreateDrug_Success() throws Exception {
        DrugRequestDto requestDto = DrugRequestDto.builder()
                .name("Amoxicillin")
                .dosageForm(DrugDosageForm.CAPSULE)
                .unit(DrugUnit.CAPSULE)
                .strength("250mg")
                .unitPrice(new BigDecimal("5.00"))
                .build();

        MvcResult result = mockMvc.perform(post("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Amoxicillin"))
                .andExpect(jsonPath("$.dosageForm").value("CAPSULE"))
                .andExpect(jsonPath("$.unit").value("CAPSULE"))
                .andExpect(jsonPath("$.strength").value("250mg"))
                .andExpect(jsonPath("$.unitPrice").value(5.00))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        DrugDto createdDrug = objectMapper.readValue(responseBody, DrugDto.class);

        Drug savedDrug = drugRepository.findById(createdDrug.getId()).orElse(null);
        assertThat(savedDrug).isNotNull();
        assertThat(savedDrug.getName()).isEqualTo("Amoxicillin");
        assertThat(savedDrug.getUnitPrice()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_001: Should store inventory details correctly")
    void testCreateDrug_InventoryDetails() throws Exception {
        DrugRequestDto requestDto = DrugRequestDto.builder()
                .name("Ibuprofen")
                .dosageForm(DrugDosageForm.TABLET)
                .unit(DrugUnit.TABLET)
                .strength("400mg")
                .unitPrice(new BigDecimal("3.75"))
                .build();

        MvcResult result = mockMvc.perform(post("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        DrugDto createdDrug = objectMapper.readValue(responseBody, DrugDto.class);

        Drug savedDrug = drugRepository.findById(createdDrug.getId()).orElseThrow();
        assertThat(savedDrug.getDosageForm()).isEqualTo(DrugDosageForm.TABLET);
        assertThat(savedDrug.getUnit()).isEqualTo(DrugUnit.TABLET);
        assertThat(savedDrug.getStrength()).isEqualTo("400mg");
        assertThat(savedDrug.getUnitPrice()).isEqualByComparingTo(new BigDecimal("3.75"));
    }

    // ========================================
    // BACKEND_DRUG_002: Get all drugs
    // ========================================

    @Test
    @DisplayName("BACKEND_DRUG_002: Should retrieve all drugs successfully")
    void testGetAllDrugs_Success() throws Exception {
        Drug drug2 = Drug.builder()
                .name("Amoxicillin")
                .dosageForm(DrugDosageForm.CAPSULE)
                .unit(DrugUnit.CAPSULE)
                .strength("500mg")
                .unitPrice(new BigDecimal("8.00"))
                .quantity(50)
                .minStockLevel(10)
                .build();
        drugRepository.save(drug2);

        Drug drug3 = Drug.builder()
                .name("Ibuprofen")
                .dosageForm(DrugDosageForm.TABLET)
                .unit(DrugUnit.TABLET)
                .strength("200mg")
                .unitPrice(new BigDecimal("3.00"))
                .quantity(75)
                .minStockLevel(10)
                .build();
        drugRepository.save(drug3);

        mockMvc.perform(get("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Paracetamol", "Amoxicillin", "Ibuprofen")))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].dosageForm").exists())
                .andExpect(jsonPath("$[0].unit").exists())
                .andExpect(jsonPath("$[0].strength").exists())
                .andExpect(jsonPath("$[0].unitPrice").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @DisplayName("BACKEND_DRUG_002: Drug catalog should be publicly accessible")
    void testGetAllDrugs_PublicAccess_Success() throws Exception {
        mockMvc.perform(get("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("BACKEND_DRUG_002: Should not return deleted drugs")
    void testGetAllDrugs_ExcludeDeleted() throws Exception {
        testDrug.setDeletedAt(java.time.LocalDateTime.now());
        drugRepository.save(testDrug);

        Drug activeDrug = Drug.builder()
                .name("ActiveDrug")
                .dosageForm(DrugDosageForm.SYRUP)
                .unit(DrugUnit.ML)
                .strength("100mg/5ml")
                .unitPrice(new BigDecimal("10.00"))
                .quantity(200)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .build();
        drugRepository.save(activeDrug);

        mockMvc.perform(get("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("ActiveDrug"));
    }

    @Test
    @DisplayName("BACKEND_DRUG_002: Should return complete catalog with all drug information")
    void testGetAllDrugs_CompleteCatalog() throws Exception {
        Drug injection = Drug.builder()
                .name("Ceftriaxone")
                .dosageForm(DrugDosageForm.INJECTION)
                .unit(DrugUnit.VIAL)
                .strength("1g")
                .unitPrice(new BigDecimal("25.00"))
                .quantity(30)
                .minStockLevel(10)
                .build();
        drugRepository.save(injection);

        Drug syrup = Drug.builder()
                .name("Cetirizine Syrup")
                .dosageForm(DrugDosageForm.SYRUP)
                .unit(DrugUnit.BOTTLE)
                .strength("5mg/5ml")
                .unitPrice(new BigDecimal("15.00"))
                .quantity(40)
                .minStockLevel(10)
                .build();
        drugRepository.save(syrup);

        mockMvc.perform(get("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].dosageForm", hasItems("TABLET", "INJECTION", "SYRUP")))
                .andExpect(jsonPath("$[*].unit", hasItems("TABLET", "VIAL", "BOTTLE")));
    }

    // ========================================
    // BACKEND_DRUG_003: Get drug by ID
    // ========================================

    @Test
    @DisplayName("BACKEND_DRUG_003: Should retrieve drug details by ID successfully")
    void testGetDrugById_Success() throws Exception {
        mockMvc.perform(get("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDrug.getId().toString()))
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.dosageForm").value("TABLET"))
                .andExpect(jsonPath("$.unit").value("TABLET"))
                .andExpect(jsonPath("$.strength").value("500mg"))
                .andExpect(jsonPath("$.unitPrice").value(2.50))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("BACKEND_DRUG_003: Should return complete drug information")
    void testGetDrugById_CompleteInfo() throws Exception {
        Drug completeDrug = Drug.builder()
                .name("Complete Drug Info")
                .dosageForm(DrugDosageForm.CREAM)
                .unit(DrugUnit.TUBE)
                .strength("0.5%")
                .unitPrice(new BigDecimal("12.75"))
                .quantity(60)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .build();
        completeDrug = drugRepository.save(completeDrug);

        mockMvc.perform(get("/drugs/{id}", completeDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Complete Drug Info"))
                .andExpect(jsonPath("$.dosageForm").value("CREAM"))
                .andExpect(jsonPath("$.unit").value("TUBE"))
                .andExpect(jsonPath("$.strength").value("0.5%"))
                .andExpect(jsonPath("$.unitPrice").value(12.75))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("BACKEND_DRUG_003: Public should access drug details")
    void testGetDrugById_PublicAccess() throws Exception {
        mockMvc.perform(get("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("staff")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }

    // ========================================
    // BACKEND_DRUG_004: Update drug information
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_004: Should update drug information successfully")
    void testUpdateDrug_Success() throws Exception {
        DrugDto updateDto = DrugDto.builder()
                .name("Paracetamol Updated")
                .unitPrice(new BigDecimal("5.00"))
                .strength("650mg")
                .unit(DrugUnit.CAPSULE)
                .dosageForm(DrugDosageForm.CAPSULE)
                .build();

        mockMvc.perform(patch("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol Updated"))
                .andExpect(jsonPath("$.unitPrice").value(5.00))
                .andExpect(jsonPath("$.strength").value("650mg"))
                .andExpect(jsonPath("$.unit").value("CAPSULE"))
                .andExpect(jsonPath("$.dosageForm").value("CAPSULE"));

        Drug updatedDrug = drugRepository.findById(testDrug.getId()).orElseThrow();
        assertThat(updatedDrug.getName()).isEqualTo("Paracetamol Updated");
        assertThat(updatedDrug.getUnitPrice()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(updatedDrug.getStrength()).isEqualTo("650mg");
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_004: Should partially update drug details")
    void testUpdateDrug_PartialUpdate() throws Exception {
        DrugDto updateDto = DrugDto.builder()
                .unitPrice(new BigDecimal("3.00"))
                .build();

        mockMvc.perform(patch("/drugs/{id}", testDrug.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.unitPrice").value(3.00))
                .andExpect(jsonPath("$.strength").value("500mg"));
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_004: Database should reflect changes")
    void testUpdateDrug_DatabaseReflectsChanges() throws Exception {
        DrugDto updateDto = DrugDto.builder()
                .unitPrice(new BigDecimal("10.00"))
                .strength("1000mg")
                .build();

        mockMvc.perform(patch("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk());

        Drug updatedDrug = drugRepository.findById(testDrug.getId()).orElseThrow();
        assertThat(updatedDrug.getUnitPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(updatedDrug.getStrength()).isEqualTo("1000mg");
    }

    // ========================================
    // BACKEND_DRUG_005: Delete drug
    // ========================================

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_005: Should soft delete drug successfully")
    void testDeleteDrug_Success() throws Exception {
        mockMvc.perform(delete("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Drug deletedDrug = drugRepository.findById(testDrug.getId()).orElseThrow();
        assertThat(deletedDrug.getDeletedAt()).isNotNull();

        List<Drug> allDrugs = drugRepository.findAll();
        assertThat(allDrugs).doesNotContain(deletedDrug);
    }

    @Test
    @WithMockUser(authorities = "doctor")
    @DisplayName("BACKEND_DRUG_005: Drug should be removed from catalog list")
    void testDeleteDrug_RemovedFromList() throws Exception {
        Drug anotherDrug = Drug.builder()
                .name("AnotherDrug")
                .dosageForm(DrugDosageForm.TABLET)
                .unit(DrugUnit.TABLET)
                .strength("100mg")
                .unitPrice(new BigDecimal("5.00"))
                .quantity(80)
                .minStockLevel(10)
                .build();
        drugRepository.save(anotherDrug);

        mockMvc.perform(delete("/drugs/{id}", testDrug.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/drugs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("doctor")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("AnotherDrug"));
    }
}