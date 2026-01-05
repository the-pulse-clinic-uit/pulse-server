package com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitlistEntryRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Duty Date is required")
    private LocalDate dutyDate;

    private String notes;

    @NotNull(message = "Priority is required. Valid values: NORMAL, PRIORITY, EMERGENCY")
    private WaitlistPriority priority;

    // Relationships
    private UUID appointmentId; // nullable - can link to existing appointment

    @NotNull(message = "Patient ID must not be empty")
    private UUID patientId;

    @NotNull(message = "Doctor ID must not be empty")
    private UUID doctorId;
}