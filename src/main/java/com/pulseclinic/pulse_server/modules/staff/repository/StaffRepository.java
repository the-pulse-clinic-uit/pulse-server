package com.pulseclinic.pulse_server.modules.staff.repository;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findByPosition(Position position);

    @Query("SELECT s FROM Staff s WHERE s.user = :user")
    Optional<Staff> findByUser(User user);

    @Query("SELECT s FROM Staff s WHERE s.department = :department")
    List<Staff> findByDepartment(Department department);

}
