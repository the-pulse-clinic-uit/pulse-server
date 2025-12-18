package com.pulseclinic.pulse_server.modules.scheduling.service.impl;

import java.time.Duration;
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
import com.pulseclinic.pulse_server.mappers.impl.DoctorMapper;
import com.pulseclinic.pulse_server.mappers.impl.ShiftAssignmentMapper;
import com.pulseclinic.pulse_server.mappers.impl.ShiftMapper;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftAssignmentRepository;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftRepository;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftService;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftMapper shiftMapper;
    private final AppointmentRepository appointmentRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftAssignmentMapper shiftAssignmentMapper;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final RoomRepository roomRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository,
                            DepartmentRepository departmentRepository,
                            ShiftMapper shiftMapper,
                            AppointmentRepository appointmentRepository,
                            ShiftAssignmentRepository shiftAssignmentRepository,
                            ShiftAssignmentMapper shiftAssignmentMapper,
                            DoctorRepository doctorRepository,
                            DoctorMapper doctorMapper,
                            RoomRepository roomRepository) {
        this.shiftRepository = shiftRepository;
        this.departmentRepository = departmentRepository;
        this.shiftMapper = shiftMapper;
        this.appointmentRepository = appointmentRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftAssignmentMapper = shiftAssignmentMapper;
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public ShiftDto createShift(ShiftRequestDto shiftRequestDto) {
        if (shiftRepository.existsByName(shiftRequestDto.getName())) {
            throw new RuntimeException("Shift name already exists");
        }

        Shift shift = Shift.builder()
                .name(shiftRequestDto.getName())
                .kind(shiftRequestDto.getKind())
                .startTime(shiftRequestDto.getStart_time())
                .endTime(shiftRequestDto.getEnd_time())
                .slotMinutes(shiftRequestDto.getSlot_minutes())
                .capacityPerSlot(shiftRequestDto.getCapacity_per_slot() != null ?
                        shiftRequestDto.getCapacity_per_slot() : 1)
                .build();

        if (shiftRequestDto.getDepartment_id() != null) {
            departmentRepository.findById(shiftRequestDto.getDepartment_id())
                    .ifPresent(shift::setDepartment);
        }

        if (shiftRequestDto.getDefault_room_id() != null) {
            roomRepository.findById(shiftRequestDto.getDefault_room_id())   
                    .ifPresent(shift::setDefaultRoom);
        }

        Shift savedShift = shiftRepository.save(shift);
        return shiftMapper.mapTo(savedShift);
    }

    @Override
    @Transactional
    public boolean updateShift(UUID shiftId, ShiftRequestDto shiftRequestDto) {
        try {
            Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
            if (shiftOpt.isEmpty()) {
                return false;
            }

            Shift shift = shiftOpt.get();

            if (shiftRequestDto.getName() != null) {
                Optional<Shift> shiftWithSameName = shiftRepository.findByName(shiftRequestDto.getName());
                if (shiftWithSameName.isPresent() && !shiftWithSameName.get().getId().equals(shiftId)) {
                    return false;
                }
                shift.setName(shiftRequestDto.getName());
            }

            if (shiftRequestDto.getKind() != null) {
                shift.setKind(shiftRequestDto.getKind());
            }
            if (shiftRequestDto.getStart_time() != null) {
                shift.setStartTime(shiftRequestDto.getStart_time());
            }
            if (shiftRequestDto.getEnd_time() != null) {
                shift.setEndTime(shiftRequestDto.getEnd_time());
            }
            if (shiftRequestDto.getSlot_minutes() != null) {
                shift.setSlotMinutes(shiftRequestDto.getSlot_minutes());
            }
            if (shiftRequestDto.getCapacity_per_slot() != null) {
                shift.setCapacityPerSlot(shiftRequestDto.getCapacity_per_slot());
            }

            shiftRepository.save(shift);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteShift(UUID shiftId) {
        try {
            Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
            if (shiftOpt.isEmpty()) {
                return false;
            }

            List<ShiftAssignment> assignments = shiftAssignmentRepository
                    .findByShiftIdAndDutyDate(shiftId, LocalDate.now());

            if (!assignments.isEmpty()) {
                throw new RuntimeException("Cannot delete shift with existing assignments");
            }

            shiftRepository.deleteById(shiftId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableSlots(UUID shiftId, LocalDate date) {
        Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
        if (shiftOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Shift shift = shiftOpt.get();
        List<LocalDateTime> slots = new ArrayList<>();

        LocalDateTime current = shift.getStartTime();
        LocalDateTime end = shift.getEndTime();

        while (current.isBefore(end)) {
            slots.add(current);
            current = current.plusMinutes(shift.getSlotMinutes());
        }

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndStartsAtBetweenAndDeletedAtIsNull(
                        null, shift.getStartTime(), shift.getEndTime());

        List<LocalDateTime> bookedSlots = appointments.stream()
                .map(Appointment::getStartsAt)
                .collect(Collectors.toList());

        slots.removeAll(bookedSlots);

        return slots;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCapacity(UUID shiftId) {
        Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
        if (shiftOpt.isEmpty()) {
            return 0;
        }

        Shift shift = shiftOpt.get();
        Integer totalSlots = calculateSlots(shiftId);

        return totalSlots * shift.getCapacityPerSlot();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftDto> getShiftById(UUID shiftId) {
        return shiftRepository.findById(shiftId)
                .map(shiftMapper::mapTo);
    }

    @Transactional(readOnly = true)
    public List<ShiftDto> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(shiftMapper::mapTo)
                .collect(Collectors.toList());
    }

    private Integer calculateSlots(UUID shiftId) {
        Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
        if (shiftOpt.isEmpty()) {
            return 0;
        }

        Shift shift = shiftOpt.get();

        if (shift.getSlotMinutes() == null || shift.getSlotMinutes() <= 0) {
            return 0;
        }

        LocalDateTime start = shift.getStartTime();
        LocalDateTime end = shift.getEndTime();

        if (start == null || end == null || !start.isBefore(end)) {
            return 0;
        }

        long totalMinutes = Duration.between(start, end).toMinutes();
        return (int) (totalMinutes / shift.getSlotMinutes());
    }
}
