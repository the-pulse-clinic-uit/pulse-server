package com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergyCheckRequest {
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotEmpty(message = "At least one drug ID is required")
    private List<UUID> drugIds;
}
