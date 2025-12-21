package com.pulseclinic.pulse_server.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal paidAmount;
    private BigDecimal outstandingDebt;
    private Map<String, BigDecimal> revenueByDepartment;
    private Map<String, BigDecimal> revenueByDoctor;
}
