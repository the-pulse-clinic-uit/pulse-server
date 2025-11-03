package com.pulseclinic.pulse_server.modules.billing.dto;

import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvoiceRequestDto {
    private InvoiceStatus status;

    // will logically set this to like idk 30 days from created_date
    private LocalDate due_date;

    private BigDecimal amount_paid;

    private BigDecimal total_amount;

    // relationships => 1
    @NotNull(message = "Encounter ID must be provided")
    private UUID encounter_id;
}
