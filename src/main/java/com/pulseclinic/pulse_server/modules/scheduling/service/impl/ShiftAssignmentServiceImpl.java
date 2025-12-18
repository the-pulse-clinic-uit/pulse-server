package com.pulseclinic.pulse_server.modules.scheduling.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ShiftAssignmentDto assignDoctor(ShiftAssignmentRequestDto shiftAssignmentRequestDto) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(shiftAssignmentRequestDto.getDoctor_id());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Optional<Shift> shiftOpt = shiftRepository.findById(shiftAssignmentRequestDto.getShift_id());
        if (shiftOpt.isEmpty()) {
            throw new RuntimeException("Shift not found");
        }

        if (checkConflicts(shiftAssignmentRequestDto.getDoctor_id(),
                          shiftAssignmentRequestDto.getShift_id(), 
                          shiftAssignmentRequestDto.getDuty_date())) {
            throw new RuntimeException("Schedule conflict detected");
        }

        ShiftAssignment assignment = ShiftAssignment.builder()
                .dutyDate(shiftAssignmentRequestDto.getDuty_date())
                .roleInShift(shiftAssignmentRequestDto.getRole_in_shift())
                .status(shiftAssignmentRequestDto.getStatus())
                .notes(shiftAssignmentRequestDto.getNotes())
                .doctor(doctorOpt.get())
                .shift(shiftOpt.get())
                .build();

        if (shiftAssignmentRequestDto.getRoom_id() != null) {
            Optional<Room> roomOpt = roomRepository.findById(shiftAssignmentRequestDto.getRoom_id());
            if (roomOpt.isPresent()) {
                assignment.setRoom(roomOpt.get());
            }
        }

        ShiftAssignment savedAssignment = shiftAssignmentRepository.save(assignment);
        return shiftAssignmentMapper.mapTo(savedAssignment);
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
