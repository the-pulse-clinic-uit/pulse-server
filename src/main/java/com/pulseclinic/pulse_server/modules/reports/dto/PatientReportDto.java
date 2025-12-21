package com.pulseclinic.pulse_server.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientReportDto {
    private LocalDate reportDate;
    private Integer newRegistrations;
    private Integer followUpVisits;
    private Integer totalPatients;
}
