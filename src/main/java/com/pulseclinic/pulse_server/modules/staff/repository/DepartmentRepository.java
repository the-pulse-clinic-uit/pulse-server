package com.pulseclinic.pulse_server.modules.staff.repository;

import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query("SELECT d FROM Department d WHERE d.deletedAt IS NULL")
    List<Department> findAll();
}
