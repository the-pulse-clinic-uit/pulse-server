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

    private LocalDate dueDate;

    private BigDecimal amountPaid;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // relationships => 1
    private EncounterDto encounterDto;
}
