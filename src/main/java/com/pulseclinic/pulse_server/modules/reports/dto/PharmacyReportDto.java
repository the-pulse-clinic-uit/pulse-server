package com.pulseclinic.pulse_server.modules.reports.dto;

import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyReportDto {
    private UUID drugId;
    private String drugName;
    private DrugDosageForm dosageForm;
    private DrugUnit unit;
    private String strength;
    private Integer currentQuantity;
    private Integer minStockLevel;
    private LocalDate expiryDate;
    private String batchNumber;
    private BigDecimal unitPrice;
    private Integer daysUntilExpiry;
    private String status; // "LOW_STOCK", "OUT_OF_STOCK", "EXPIRING_SOON"
}
