package com.pulseclinic.pulse_server.modules.staff.dto.department;

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
public class DepartmentDto {
    private UUID id;

    private String name;

    private String description;

    private LocalDateTime createdAt;
}
