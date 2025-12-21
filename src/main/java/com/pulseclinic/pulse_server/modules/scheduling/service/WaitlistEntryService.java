package com.pulseclinic.pulse_server.modules.scheduling.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryRequestDto;

public interface WaitlistEntryService {
    WaitlistEntryDto addToWaitlist(WaitlistEntryRequestDto waitlistEntryRequestDto);
    Optional<WaitlistEntryDto> callNext(UUID departmentId);
    boolean changePriority(UUID entryId, WaitlistPriority priority);
    boolean markAsServed(UUID entryId);
    boolean cancelEntry(UUID entryId);
    Integer getWaitingCount(UUID departmentId);
    Optional<WaitlistEntryDto> getEntryById(UUID entryId);
    List<WaitlistEntryDto> findAll();
}
