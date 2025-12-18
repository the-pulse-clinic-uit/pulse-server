package com.pulseclinic.pulse_server.modules.staff.controller;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.mappers.impl.StaffMapper;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;
    private final StaffMapper staffMapper;

    public StaffController(StaffService staffService, StaffMapper staffMapper) {
        this.staffService = staffService;
        this.staffMapper = staffMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<StaffDto> createStaff(@RequestBody StaffRequestDto staffRequestDto) {
        Staff staff = this.staffService.createStaff(staffRequestDto);
        return new ResponseEntity<>(this.staffMapper.mapTo(staff), HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StaffDto>> searchStaff(@RequestParam Position position) {
        List<Staff> staff = this.staffService.searchByPosition(position);
        return new ResponseEntity<>(staff.stream().map(staff1 -> this.staffMapper.mapTo(staff1)).collect(Collectors.toList()), HttpStatus.OK);
    }
    @GetMapping("/me")
    public ResponseEntity<StaffDto> getStaffMe(Authentication authentication){
        Optional<Staff> staff = this.staffService.findByEmail(authentication.getName());
        if(staff.isPresent()){
            return new ResponseEntity<>(this.staffMapper.mapTo(staff.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable UUID id) {
        Optional<Staff> staff = this.staffService.findById(id);
        if(staff.isPresent()){
            return new ResponseEntity<>(this.staffMapper.mapTo(staff.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PatchMapping("/me")
    public ResponseEntity<StaffDto> updateStaffMe(Authentication authentication,@RequestBody StaffDto staffDto) {
        Staff staff = this.staffService.updateMe(authentication.getName(), staffDto);
        return new ResponseEntity<>(this.staffMapper.mapTo(staff), HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<StaffDto> updateStaffById(@PathVariable UUID id, @RequestBody StaffDto staffDto) {
        Staff staff = this.staffService.update(id, staffDto);
        return new ResponseEntity<>(this.staffMapper.mapTo(staff), HttpStatus.OK);
    }

}
