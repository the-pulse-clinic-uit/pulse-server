package com.pulseclinic.pulse_server.modules.scheduling.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.service.WaitlistEntryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/waitlist")
public class WaitlistEntryController {
    private final WaitlistEntryService waitlistEntryService;

    public WaitlistEntryController(WaitlistEntryService waitlistEntryService) {
        this.waitlistEntryService = waitlistEntryService;
    }

    // Add patient to waitlist
    @PostMapping
    public ResponseEntity<WaitlistEntryDto> addToWaitlist(@Valid @RequestBody WaitlistEntryRequestDto requestDto) {
        try {
            WaitlistEntryDto entry = waitlistEntryService.addToWaitlist(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all waitlist entries
    @GetMapping
    public ResponseEntity<List<WaitlistEntryDto>> getAllEntries() {
        List<WaitlistEntryDto> entries = waitlistEntryService.findAll();
        return ResponseEntity.ok(entries);
    }

    // Get entry by ID
    @GetMapping("/{entryId}")
    public ResponseEntity<WaitlistEntryDto> getEntryById(@PathVariable UUID entryId) {
        Optional<WaitlistEntryDto> entry = waitlistEntryService.getEntryById(entryId);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Call next patient in department queue
    @PostMapping("/department/{departmentId}/call_next")
    public ResponseEntity<WaitlistEntryDto> callNext(@PathVariable UUID departmentId) {
        Optional<WaitlistEntryDto> entry = waitlistEntryService.callNext(departmentId);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update priority
    @PutMapping("/{entryId}/priority")
    public ResponseEntity<Void> changePriority(
            @PathVariable UUID entryId,
            @RequestParam WaitlistPriority priority) {
        boolean success = waitlistEntryService.changePriority(entryId, priority);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Mark as served
    @PutMapping("/{entryId}/served")
    public ResponseEntity<Void> markAsServed(@PathVariable UUID entryId) {
        boolean success = waitlistEntryService.markAsServed(entryId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Cancel entry
    @PutMapping("/{entryId}/cancel")
    public ResponseEntity<Void> cancelEntry(@PathVariable UUID entryId) {
        boolean success = waitlistEntryService.cancelEntry(entryId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Get waiting count for department
    @GetMapping("/department/{departmentId}/waiting/count")
    public ResponseEntity<Integer> getWaitingCount(@PathVariable UUID departmentId) {
        Integer count = waitlistEntryService.getWaitingCount(departmentId);
        return ResponseEntity.ok(count);
    }
}
