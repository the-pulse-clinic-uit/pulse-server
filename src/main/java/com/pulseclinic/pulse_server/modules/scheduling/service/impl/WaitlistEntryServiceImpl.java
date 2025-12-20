package com.pulseclinic.pulse_server.modules.scheduling.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.enums.WaitlistStatus;
import com.pulseclinic.pulse_server.mappers.impl.WaitlistEntryMapper;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry.WaitlistEntryRequestDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.WaitlistEntry;
import com.pulseclinic.pulse_server.modules.scheduling.repository.WaitlistEntryRepository;
import com.pulseclinic.pulse_server.modules.scheduling.service.WaitlistEntryService;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;

@Service
public class WaitlistEntryServiceImpl implements WaitlistEntryService {

    private final WaitlistEntryRepository waitlistEntryRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final WaitlistEntryMapper waitlistEntryMapper;

    public WaitlistEntryServiceImpl(WaitlistEntryRepository waitlistEntryRepository,
                                   PatientRepository patientRepository,
                                   DoctorRepository doctorRepository,
                                   AppointmentRepository appointmentRepository,
                                   WaitlistEntryMapper waitlistEntryMapper) {
        this.waitlistEntryRepository = waitlistEntryRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.waitlistEntryMapper = waitlistEntryMapper;
    }

    @Override
    @Transactional
    public WaitlistEntryDto addToWaitlist(WaitlistEntryRequestDto waitlistEntryRequestDto) {
        // Tìm patient
        Optional<Patient> patientOpt = patientRepository.findById(waitlistEntryRequestDto.getPatientId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        // Tìm doctor
        Optional<Doctor> doctorOpt = doctorRepository.findById(waitlistEntryRequestDto.getDoctorId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        // Tạo ticket number
        Integer ticketNo = generateTicketNumber(waitlistEntryRequestDto.getDoctorId(),
                                                waitlistEntryRequestDto.getDutyDate());

        WaitlistEntry entry = WaitlistEntry.builder()
                .dutyDate(waitlistEntryRequestDto.getDutyDate())
                .ticketNo(ticketNo)
                .notes(waitlistEntryRequestDto.getNotes())
                .priority(waitlistEntryRequestDto.getPriority())
                .status(WaitlistStatus.WAITING)
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .build();

        // Set appointment nếu có
        if (waitlistEntryRequestDto.getAppointmentId() != null) {
            appointmentRepository.findById(waitlistEntryRequestDto.getAppointmentId())
                    .ifPresent(entry::setAppointment);
        }

        WaitlistEntry savedEntry = waitlistEntryRepository.save(entry);
        return waitlistEntryMapper.mapTo(savedEntry);
    }

    @Override
    @Transactional
    public Optional<WaitlistEntryDto> callNext(UUID departmentId) {
        List<WaitlistEntry> queue = waitlistEntryRepository.findNextInQueueByDepartment(
                departmentId, LocalDate.now(), WaitlistStatus.WAITING);

        if (queue.isEmpty()) {
            return Optional.empty();
        }

        WaitlistEntry nextEntry = queue.get(0);
        nextEntry.setStatus(WaitlistStatus.CALLED);
        nextEntry.setCalledAt(LocalDateTime.now());
        
        WaitlistEntry updatedEntry = waitlistEntryRepository.save(nextEntry);
        return Optional.of(waitlistEntryMapper.mapTo(updatedEntry));
    }

    @Override
    @Transactional
    public boolean changePriority(UUID entryId, WaitlistPriority priority) {
        try {
            Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
            if (entryOpt.isEmpty()) {
                return false;
            }

            WaitlistEntry entry = entryOpt.get();
            entry.setPriority(priority);
            waitlistEntryRepository.save(entry);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean markAsServed(UUID entryId) {
        try {
            Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
            if (entryOpt.isEmpty()) {
                return false;
            }

            WaitlistEntry entry = entryOpt.get();
            entry.setStatus(WaitlistStatus.SERVED);
            entry.setServedAt(LocalDateTime.now());
            waitlistEntryRepository.save(entry);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelEntry(UUID entryId) {
        try {
            Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
            if (entryOpt.isEmpty()) {
                return false;
            }

            WaitlistEntry entry = entryOpt.get();
            entry.setStatus(WaitlistStatus.CANCELLED);
            waitlistEntryRepository.save(entry);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getWaitingCount(UUID departmentId) {
        return waitlistEntryRepository.countWaitingByDepartment(departmentId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WaitlistEntryDto> getEntryById(UUID entryId) {
        return waitlistEntryRepository.findById(entryId)
                .map(waitlistEntryMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WaitlistEntryDto> findAll() {
        return waitlistEntryRepository.findAll().stream()
                .map(waitlistEntryMapper::mapTo)
                .collect(Collectors.toList());
    }

    private Integer generateTicketNumber(UUID doctorId, LocalDate dutyDate) {
        Integer count = waitlistEntryRepository.countWaiting(doctorId, dutyDate);
        return count + 1;
    }
}
