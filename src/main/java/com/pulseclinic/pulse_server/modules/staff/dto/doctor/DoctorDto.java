package com.pulseclinic.pulse_server.modules.staff.dto.doctor;

import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DoctorDto {
    private UUID id;

    private String license_id;

    private Boolean is_verified;

    private LocalDateTime created_at;

    // relationships => 2

    private StaffDto staffDto;

    private DepartmentDto department_dto;
}
