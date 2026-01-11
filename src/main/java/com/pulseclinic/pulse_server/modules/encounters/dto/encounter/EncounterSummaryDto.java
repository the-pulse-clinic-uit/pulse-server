package com.pulseclinic.pulse_server.modules.encounters.dto.encounter;

import com.pulseclinic.pulse_server.enums.EncounterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EncounterSummaryDto {
    private UUID id;
    private EncounterType type;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String diagnosis;
    private String notes;
    private LocalDateTime createdAt;

    // Lightweight references - just IDs and names
    private UUID patientId;
    private String patientName;

    private UUID doctorId;
    private String doctorName;

    private UUID appointmentId;
}
