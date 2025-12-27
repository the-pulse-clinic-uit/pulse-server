package com.pulseclinic.pulse_server.modules.staff.controller;

import com.pulseclinic.pulse_server.mappers.impl.DepartmentMapper;
import com.pulseclinic.pulse_server.mappers.impl.StaffMapper;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentRequestDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentStatisticsDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffRequestDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentMapper departmentMapper;
    private final DepartmentService departmentService;
    private final StaffMapper staffMapper;

    public DepartmentController(DepartmentService departmentService,
            DepartmentMapper departmentMapper,
            StaffMapper staffMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
        this.staffMapper = staffMapper;
    }

    @PostMapping("/{id}/staff")
    // @PreAuthorize("hasAnyAuthority('doctor, staff, doctor')")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<HttpStatus> assignStaff(@PathVariable UUID id, @RequestBody UUID staffId) {
        if (this.departmentService.assignStaff(id, staffId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}/staff/{staffId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<HttpStatus> deleteStaff(@PathVariable UUID id, @PathVariable UUID staffId) {
        if (this.departmentService.unassignStaff(id, staffId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentRequestDto departmentRequestDto) {
        Department department = this.departmentService.create(this.departmentMapper.mapFrom(departmentRequestDto));
        return new ResponseEntity<>(this.departmentMapper.mapTo(department), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/staff")
    public ResponseEntity<List<StaffDto>> getAllStaff(@PathVariable UUID id) {
        List<Staff> staff = this.departmentService.findAllStaff(id);
        return new ResponseEntity<>(staff.stream().map(s -> this.staffMapper.mapTo(s)).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<DepartmentStatisticsDto> getDepartmentStatistics(@PathVariable UUID id) {
        DepartmentStatisticsDto statistics = this.departmentService.getDepartmentStatistics(id);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getById(@PathVariable UUID id) {
        Optional<Department> department = this.departmentService.findById(id);
        if (department.isPresent()) {
            return new ResponseEntity<>(this.departmentMapper.mapTo(department.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<DepartmentDto>> getAll() {
        List<Department> departments = this.departmentService.findAll(); // deleted is null
        return new ResponseEntity<>(
                departments.stream().map(d -> this.departmentMapper.mapTo(d)).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable UUID id, @RequestBody DepartmentDto departmentDto) {
        Department department = this.departmentService.update(id, departmentDto);
        return new ResponseEntity<>(this.departmentMapper.mapTo(department), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.departmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
