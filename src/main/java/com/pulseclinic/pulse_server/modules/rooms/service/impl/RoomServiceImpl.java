package com.pulseclinic.pulse_server.modules.rooms.service.impl;

import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.rooms.service.RoomService;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;
    public RoomServiceImpl(RoomRepository roomRepository, DepartmentRepository departmentRepository) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Room createRoom(Room room) {
        return this.roomRepository.save(room);
    }

    @Override
    public List<Room> findAll() {
        return this.roomRepository.findAll();
    }

    @Override
    public List<Room> findAllByDepartmentId(UUID departmentId) {
        Optional<Department> department = this.departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            return this.roomRepository.findAllByDepartment(department.get());
        }
        else throw new RuntimeException("Department not found");
    }

    @Override
    public Room findById(UUID id) {
        return this.roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public Room updateStatus(UUID id) {
        Room room = this.roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        Boolean currentStatus = room.getIsAvailable();
        room.setIsAvailable(!currentStatus);
        return this.roomRepository.save(room);
    }

    @Override
    public void deleteRoom(UUID id) {
        Room room = this.roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setDeletedAt(LocalDateTime.now());
        this.roomRepository.save(room);
    }

    @Override
    public Room updateRoom(UUID id, RoomDto roomDto) {
        Room room = this.roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        if (roomDto.getIsAvailable() != null) {
            room.setIsAvailable(roomDto.getIsAvailable());
        }
        if (roomDto.getRoomNumber() != null) {
            room.setRoomNumber(roomDto.getRoomNumber());
        }
        if (roomDto.getBedAmount() != null) {
            room.setBedAmount(roomDto.getBedAmount());
        }
        if (roomDto.getDepartmentDto() != null) {
            Department department = this.departmentRepository.findById(roomDto.getDepartmentDto().getId()).orElseThrow(() -> new RuntimeException("Department not found"));
            room.setDepartment(department);
        }
        return this.roomRepository.save(room);
    }
}
