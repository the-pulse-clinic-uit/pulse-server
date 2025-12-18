package com.pulseclinic.pulse_server.modules.rooms.repository;

import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("SELECT r FROM Room r WHERE r.deletedAt IS NULL")
    List<Room> findAll();

    @Query("SELECT r FROM Room r WHERE r.department = :department")
    List<Room> findAllByDepartment(Department department);
}
