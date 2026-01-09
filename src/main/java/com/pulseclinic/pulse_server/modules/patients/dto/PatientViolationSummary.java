package com.pulseclinic.pulse_server.modules.patients.dto;

import com.pulseclinic.pulse_server.enums.ViolationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientViolationSummary {
    private Integer noShowCount;
    private BigDecimal outstandingDebt;
    private LocalDateTime lastViolationDate;
    private ViolationLevel riskLevel;
    private boolean hasViolations;
    private String message;
}
