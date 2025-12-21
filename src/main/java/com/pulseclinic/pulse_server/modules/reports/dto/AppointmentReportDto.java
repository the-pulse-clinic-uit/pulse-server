package com.pulseclinic.pulse_server.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReportDto {
    private LocalDate reportDate;
    private Integer totalAppointments;
    private Integer confirmed;
    private Integer completed;
    private Integer cancelled;
    private Integer noShow;
    private Map<String, Integer> byDepartment;
    private Map<String, Integer> byDoctor;
}
