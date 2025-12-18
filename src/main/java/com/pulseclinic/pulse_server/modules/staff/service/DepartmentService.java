package com.pulseclinic.pulse_server.modules.staff.service;

import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentRequestDto;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentStatisticsDto;
import com.pulseclinic.pulse_server.modules.staff.dto.staff.StaffDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentService {
    Department create(Department department);
    Optional<Department> findById(UUID id);
    List<Department> findAll();
    void delete(UUID id);
    Department update(UUID id, DepartmentDto departmentDto);
    List<Staff> findAllStaff(UUID id);
    Boolean assignStaff(UUID id, UUID staffId);
    Boolean unassignStaff(UUID id, UUID staffId);
    DepartmentStatisticsDto getDepartmentStatistics(UUID id);
}
