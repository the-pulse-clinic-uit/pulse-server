package com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy;

import com.pulseclinic.pulse_server.enums.AllergySeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergyWarning {
    private UUID drugId;
    private String drugName;
    private String allergen;
    private AllergySeverity severity;
    private String message;
}
