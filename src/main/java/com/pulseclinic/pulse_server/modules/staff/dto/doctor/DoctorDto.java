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

    private String licenseId;

    private Boolean isVerified;

    private LocalDateTime createdAt;

    private Double averageRating;

    private Integer ratingCount;

    // relationships => 1 (department is derived from staff.department)

    private StaffDto staffDto;

    private DepartmentDto departmentDto; // Derived from staff.department
}
