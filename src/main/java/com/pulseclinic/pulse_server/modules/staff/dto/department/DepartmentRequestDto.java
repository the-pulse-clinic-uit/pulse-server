package com.pulseclinic.pulse_server.modules.staff.dto.department;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DepartmentRequestDto {
    @NotNull(message = "Name is required")
    private String name;

    @Size(min = 1, max = 100, message = "Description is 1 to 100")
    private String description;
}
