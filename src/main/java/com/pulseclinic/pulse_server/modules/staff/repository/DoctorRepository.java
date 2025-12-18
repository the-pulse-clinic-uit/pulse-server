package com.pulseclinic.pulse_server.modules.staff.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByLicenseId(String licenseId);
    Optional<Doctor> findByStaffId(UUID staffId);
    List<Doctor> findByDepartmentId(UUID departmentId);
    boolean existsByLicenseId(String licenseId);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.department.id = :departmentId")
    Integer countByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.department = :department")
    Integer countByDepartment(@Param("department") Department department);
}
