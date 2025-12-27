package com.pulseclinic.pulse_server.modules.scheduling.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/shifts")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<ShiftDto> createShift(@Valid @RequestBody ShiftRequestDto shiftRequestDto) {
        try {
            ShiftDto shift = shiftService.createShift(shiftRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(shift);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('staff', 'doctor')")
    public ResponseEntity<List<ShiftDto>> getAllShifts() {
        List<ShiftDto> shifts = shiftService.getAllShifts();
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/{shiftId}")
    @PreAuthorize("hasAnyAuthority('staff', 'doctor')")
    public ResponseEntity<ShiftDto> getShiftById(@PathVariable UUID shiftId) {
        Optional<ShiftDto> shift = shiftService.getShiftById(shiftId);
        return shift.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{shiftId}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<Void> updateShift(
            @PathVariable UUID shiftId,
            @Valid @RequestBody ShiftRequestDto shiftRequestDto) {
        boolean updated = shiftService.updateShift(shiftId, shiftRequestDto);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{shiftId}")
    @PreAuthorize("hasAuthority('doctor')")
    public ResponseEntity<Void> deleteShift(@PathVariable UUID shiftId) {
        boolean deleted = shiftService.deleteShift(shiftId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{shiftId}/slots/available")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @PathVariable UUID shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalDateTime> slots = shiftService.getAvailableSlots(shiftId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{shiftId}/capacity")
    public ResponseEntity<Integer> getCapacity(@PathVariable UUID shiftId) {
        Integer capacity = shiftService.getCapacity(shiftId);
        return ResponseEntity.ok(capacity);
    }
}
