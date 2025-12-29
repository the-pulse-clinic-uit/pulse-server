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

    @Query("SELECT d FROM Doctor d JOIN d.staff s JOIN s.user u WHERE u.email = :email AND d.deletedAt IS NULL")
    Optional<Doctor> findByEmail(@Param("email") String email);

    @Query("SELECT d FROM Doctor d WHERE d.staff.department.id = :departmentId")
    List<Doctor> findByDepartmentId(@Param("departmentId") UUID departmentId);

    boolean existsByLicenseId(String licenseId);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.staff.department.id = :departmentId")
    Integer countByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.staff.department = :department")
    Integer countByDepartment(@Param("department") Department department);
}
