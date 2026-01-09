package com.pulseclinic.pulse_server.modules.pharmacy.dto.drug;

import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DrugDto {
    private UUID id;

    private String name;

    private DrugDosageForm dosageForm;

    private DrugUnit unit = DrugUnit.CAPSULE;

    private String strength;

    private LocalDateTime createdAt;

    private BigDecimal unitPrice;

    private Integer quantity;

    private LocalDate expiryDate;

    private Integer minStockLevel;

    private String batchNumber;
}
