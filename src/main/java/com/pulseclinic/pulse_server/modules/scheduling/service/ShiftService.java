package com.pulseclinic.pulse_server.modules.scheduling.service;

import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftService {
    ShiftDto createShift(ShiftRequestDto shiftRequestDto);
    boolean updateShift(UUID shiftId, ShiftRequestDto shiftRequestDto);
    boolean deleteShift(UUID shiftId);
    List<LocalDateTime> getAvailableSlots(UUID shiftId, LocalDate date);
    Integer getCapacity(UUID shiftId);
    Optional<ShiftDto> getShiftById(UUID shiftId);
    List<ShiftDto> getAllShifts();
}
