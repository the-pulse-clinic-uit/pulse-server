package com.pulseclinic.pulse_server.modules.billing.dto;

import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
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
public class InvoiceDto {
    private UUID id;

    private InvoiceStatus status;

    private LocalDate due_date;

    private BigDecimal amount_paid;

    private BigDecimal total_amount;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    // relationships => 1
    private EncounterDto encounter_dto;
}
