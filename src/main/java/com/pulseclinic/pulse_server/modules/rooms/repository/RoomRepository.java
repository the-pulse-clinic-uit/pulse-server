package com.pulseclinic.pulse_server.modules.rooms.repository;

import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
}
