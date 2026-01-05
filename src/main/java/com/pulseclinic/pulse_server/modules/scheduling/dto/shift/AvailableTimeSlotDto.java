package com.pulseclinic.pulse_server.modules.scheduling.dto.shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableTimeSlotDto {
    private LocalDateTime startsAt;
    private long capacity;
}
