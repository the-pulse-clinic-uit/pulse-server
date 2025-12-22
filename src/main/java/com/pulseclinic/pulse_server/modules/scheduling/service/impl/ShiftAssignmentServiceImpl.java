package com.pulseclinic.pulse_server.modules.scheduling.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.mappers.impl.AppointmentMapper;
import com.pulseclinic.pulse_server.mappers.impl.ShiftAssignmentMapper;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftAssignmentRepository;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftRepository;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftAssignmentService;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;

@Slf4j
@Service
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final DoctorRepository doctorRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftAssignmentMapper shiftAssignmentMapper;
    private final RoomRepository roomRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepository shiftAssignmentRepository,
                                      DoctorRepository doctorRepository,
                                      ShiftRepository shiftRepository,
                                      ShiftAssignmentMapper shiftAssignmentMapper,
                                      RoomRepository roomRepository,
                                      AppointmentRepository appointmentRepository,
                                      AppointmentMapper appointmentMapper) {
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.doctorRepository = doctorRepository;
        this.shiftRepository = shiftRepository;
        this.shiftAssignmentMapper = shiftAssignmentMapper;
        this.roomRepository = roomRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional
    public ShiftAssignmentDto assignDoctor(ShiftAssignmentRequestDto dto) {

        LocalDate date =  dto.getDutyDate() == null ? null :  dto.getDutyDate();
        log.info("Duty date received: {}", date.isBefore(LocalDate.now()) );
        if (dto.getDutyDate() == null || dto.getDutyDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Invalid duty date");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        String debug = doctor.getDepartment().toString();
        log.info("Debugging: {}", debug);

        if (shift.getDepartment() != null) {
            if (doctor.getDepartment() == null || doctor.getDepartment() == null) {
                throw new RuntimeException("Doctor has no department");
            }
            if (!shift.getDepartment().getId()
                    .equals(doctor.getDepartment().getId())) {
                throw new RuntimeException("Doctor does not belong to shift department");
            }
        }

        if (shiftAssignmentRepository.existsByDoctorIdAndShiftIdAndDutyDate(
                doctor.getId(), shift.getId(), dto.getDutyDate())) {
            throw new RuntimeException("Duplicate shift assignment");
        }

        if (checkConflicts(dto.getDoctorId(), dto.getShiftId(), dto.getDutyDate())) {
            throw new RuntimeException("Schedule conflict detected");
        }

        ShiftAssignmentRole role =
                dto.getRoleInShift() != null ? dto.getRoleInShift() : ShiftAssignmentRole.PRIMARY;

        ShiftAssignmentStatus status =
                dto.getStatus() != null ? dto.getStatus() : ShiftAssignmentStatus.ACTIVE;

        ShiftAssignment assignment = ShiftAssignment.builder()
                .dutyDate(dto.getDutyDate())
                .roleInShift(role)
                .status(status)
                .notes(dto.getNotes())
                .doctor(doctor)
                .shift(shift)
                .build();

        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            if (shift.getDepartment() != null && room.getDepartment() != null) {
                if (!shift.getDepartment().getId().equals(room.getDepartment().getId())) {
                    throw new RuntimeException("Room does not belong to shift department");
                }
            }

            assignment.setRoom(room);
        } else if (shift.getDefaultRoom() != null) {
            assignment.setRoom(shift.getDefaultRoom());
        }

        ShiftAssignment saved = shiftAssignmentRepository.save(assignment);
        return shiftAssignmentMapper.mapTo(saved);
    }


    @Override
    @Transactional
    public boolean updateStatus(UUID assignmentId, ShiftAssignmentStatus status) {
        try {
            Optional<ShiftAssignment> assignmentOpt = shiftAssignmentRepository.findById(assignmentId);
            if (assignmentOpt.isEmpty()) {
                return false;
            }

            ShiftAssignment assignment = assignmentOpt.get();
            assignment.setStatus(status);
            shiftAssignmentRepository.save(assignment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateRoom(UUID assignmentId, UUID roomId) {
        try {
            Optional<ShiftAssignment> assignmentOpt = shiftAssignmentRepository.findById(assignmentId);
            if (assignmentOpt.isEmpty()) {
                return false;
            }

            ShiftAssignment assignment = assignmentOpt.get();

            // Find and set room
            Optional<Room> roomOpt = roomRepository.findById(roomId);
            if (roomOpt.isEmpty()) {
                return false;
            }

            assignment.setRoom(roomOpt.get());
            shiftAssignmentRepository.save(assignment);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftAssignmentDto> findByDoctor(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        return shiftAssignmentRepository.findByDoctorIdAndDutyDateBetween(doctorId, startDate, endDate).stream()
                .map(shiftAssignmentMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftAssignmentDto> findByShift(UUID shiftId, LocalDate date) {
        return shiftAssignmentRepository.findByShiftIdAndDutyDate(shiftId, date).stream()
                .map(shiftAssignmentMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftAssignmentDto> getAssignmentById(UUID assignmentId) {
        return shiftAssignmentRepository.findById(assignmentId)
                .map(shiftAssignmentMapper::mapTo);
    }

    @Transactional(readOnly = true)
    public List<ShiftAssignmentDto> getAllAssignments() {
        return shiftAssignmentRepository.findAll().stream()
                .map(shiftAssignmentMapper::mapTo)
                .collect(Collectors.toList());
    }

    private boolean checkConflicts(UUID doctorId, UUID shiftId, LocalDate date) {
        List<ShiftAssignment> conflicts = shiftAssignmentRepository.findConflicts(doctorId, shiftId, date);
        return !conflicts.isEmpty();
    }
}
