package com.pulseclinic.pulse_server.modules.staff.dto.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DepartmentStatisticsDto {
    private Integer doctorCount;
    private Double totalRevenue;
    private Integer totalAppointments;
}
