package com.pulseclinic.pulse_server.modules.encounters.dto.encounter;

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
public class DoctorRatingDto {
    private UUID encounterId;
    private Integer rating;
    private String comment;
    private LocalDateTime ratedAt;
    private String patientName;
}
