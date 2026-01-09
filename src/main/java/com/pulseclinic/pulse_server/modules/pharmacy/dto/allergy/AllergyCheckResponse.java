package com.pulseclinic.pulse_server.modules.pharmacy.dto.allergy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergyCheckResponse {
    private boolean hasWarnings;
    private List<AllergyWarning> warnings;
    private String message;
}
