package com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription;

import com.pulseclinic.pulse_server.enums.PrescriptionStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PrescriptionDto {
    private UUID id;

    private BigDecimal total_price;

    private String notes;

    private LocalDateTime created_at;

    private PrescriptionStatus status;

    // relationship
    private EncounterDto encounter_dto;
}
