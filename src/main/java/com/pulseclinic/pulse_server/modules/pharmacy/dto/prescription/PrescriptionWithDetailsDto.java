package com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription;

import com.pulseclinic.pulse_server.enums.PrescriptionStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.pharmacy.dto.prescriptionDetail.PrescriptionDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PrescriptionWithDetailsDto {
    private UUID id;

    private BigDecimal totalPrice;

    private String notes;

    private LocalDateTime createdAt;

    private PrescriptionStatus status;

    private EncounterDto encounterDto;

    private List<PrescriptionDetailDto> prescriptionDetails;
}
