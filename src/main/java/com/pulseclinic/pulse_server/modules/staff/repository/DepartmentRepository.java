package com.pulseclinic.pulse_server.modules.staff.repository;

import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}
