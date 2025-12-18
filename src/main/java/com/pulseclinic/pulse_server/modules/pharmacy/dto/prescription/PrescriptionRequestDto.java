package com.pulseclinic.pulse_server.modules.pharmacy.dto.prescription;

import com.pulseclinic.pulse_server.enums.PrescriptionStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PrescriptionRequestDto {
    @Positive(message = "Total price must be provided and positive")
    private BigDecimal totalPrice;

    private String notes;

    private PrescriptionStatus status;

    // relationship
    @NotNull(message = "Encounter ID must not be empty")
    private UUID encounterId;
}
