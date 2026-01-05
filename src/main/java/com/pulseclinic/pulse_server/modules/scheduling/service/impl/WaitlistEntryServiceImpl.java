package com.pulseclinic.pulse_server.modules.scheduling.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
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
                                    WaitlistEntryMapper waitlistEntryMapper, DepartmentRepository departmentRepository) {
        this.waitlistEntryRepository = waitlistEntryRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.waitlistEntryMapper = waitlistEntryMapper;
    }

    @Override
    @Transactional
    public WaitlistEntryDto addToWaitlist(WaitlistEntryRequestDto waitlistEntryRequestDto) {
        Patient patient = findPatientById(waitlistEntryRequestDto.getPatientId());
        Doctor doctor = findDoctorById(waitlistEntryRequestDto.getDoctorId());
        validateDutyDate(waitlistEntryRequestDto.getDutyDate());

        Integer ticketNo = generateTicketNumber(
                doctor.getStaff().getDepartment().getId(),
                waitlistEntryRequestDto.getDutyDate()
        );

        WaitlistEntry entry = buildWaitlistEntry(waitlistEntryRequestDto, patient, doctor, ticketNo);
        attachAppointmentIfExists(entry, waitlistEntryRequestDto.getAppointmentId());

        WaitlistEntry savedEntry = waitlistEntryRepository.save(entry);
        return waitlistEntryMapper.mapTo(savedEntry);
    }

    @Override
    @Transactional
    public Optional<WaitlistEntryDto> callNext(UUID departmentId, LocalDate dutyDate) {
        List<WaitlistEntry> queue = findNextInQueue(departmentId, dutyDate);

        if (queue.isEmpty()) {
            return Optional.empty();
        }

        WaitlistEntry nextEntry = queue.get(0);
        markEntryAsCalled(nextEntry);

        WaitlistEntry updatedEntry = waitlistEntryRepository.save(nextEntry);
        return Optional.of(waitlistEntryMapper.mapTo(updatedEntry));
    }

    @Override
    @Transactional
    public boolean changePriority(UUID entryId, WaitlistPriority priority) {
        Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return false;
        }

        WaitlistEntry entry = entryOpt.get();
        updatePriority(entry, priority);
        waitlistEntryRepository.save(entry);
        return true;
    }

    @Override
    @Transactional
    public boolean markAsServed(UUID entryId) {
        Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return false;
        }

        WaitlistEntry entry = entryOpt.get();
        markEntryAsServed(entry);
        waitlistEntryRepository.save(entry);
        return true;
    }

    @Override
    @Transactional
    public boolean markAsNoShow(UUID entryId) {
        Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return false;
        }

        WaitlistEntry entry = entryOpt.get();

        // Only allow marking as NO_SHOW if status is CALLED
        if (entry.getStatus() != WaitlistStatus.CALLED) {
            return false;
        }

        markEntryAsNoShow(entry);
        waitlistEntryRepository.save(entry);
        return true;
    }

    @Override
    @Transactional
    public boolean cancelEntry(UUID entryId) {
        Optional<WaitlistEntry> entryOpt = waitlistEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return false;
        }

        WaitlistEntry entry = entryOpt.get();
        markEntryAsCancelled(entry);
        waitlistEntryRepository.save(entry);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getWaitingCount(UUID departmentId, LocalDate dutyDate) {
        return waitlistEntryRepository.countWaitingByDepartment(departmentId, dutyDate);
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

    @Override
    @Transactional(readOnly = true)
    public List<WaitlistEntryDto> findAllByDepartmentId(UUID departmentId) {
        return waitlistEntryRepository.findByDepartment(departmentId)
                .stream().map(waitlistEntryMapper::mapTo)
                .collect(Collectors.toList());
    }

    // ============================================
    // PRIVATE UTILITY METHODS (Following SRP)
    // ============================================

    /**
     * Find patient by ID
     * SRP: Single responsibility - find patient entity
     */
    private Patient findPatientById(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
    }

    /**
     * Find doctor by ID
     * SRP: Single responsibility - find doctor entity
     */
    private Doctor findDoctorById(UUID doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
    }

    /**
     * Validate duty date is not in the past
     * SRP: Single responsibility - date validation
     */
    private void validateDutyDate(LocalDate dutyDate) {
        if (dutyDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Duty date must not be before today");
        }
    }

    /**
     * Generate ticket number in format: D001-20231230-001
     * SRP: Single responsibility - generate unique ticket number
     */
    private Integer generateTicketNumber(UUID departmentId, LocalDate dutyDate) {
        Integer maxTicket = waitlistEntryRepository.findMaxTicketNoByDepartment(departmentId, dutyDate);
        return (maxTicket != null ? maxTicket : 0) + 1;
    }

    /**
     * Build WaitlistEntry entity from request DTO
     * SRP: Single responsibility - construct entity
     */
    private WaitlistEntry buildWaitlistEntry(WaitlistEntryRequestDto dto, Patient patient,
                                             Doctor doctor, Integer ticketNo) {
        return WaitlistEntry.builder()
                .dutyDate(dto.getDutyDate())
                .ticketNo(ticketNo)
                .notes(dto.getNotes())
                .priority(dto.getPriority())
                .status(WaitlistStatus.WAITING)
                .patient(patient)
                .doctor(doctor)
                .build();
    }

    /**
     * Attach appointment to entry if appointmentId exists
     * SRP: Single responsibility - link appointment
     */
    private void attachAppointmentIfExists(WaitlistEntry entry, UUID appointmentId) {
        if (appointmentId != null) {
            appointmentRepository.findById(appointmentId)
                    .ifPresent(entry::setAppointment);
        }
    }

    /**
     * Find next entries in queue for a department
     * SRP: Single responsibility - query queue
     */
    private List<WaitlistEntry> findNextInQueue(UUID departmentId, LocalDate dutyDate) {
        return waitlistEntryRepository.findNextInQueueByDepartment(
                departmentId,
                dutyDate,
                WaitlistStatus.WAITING
        );
    }

    /**
     * Mark entry as CALLED and set timestamp
     * SRP: Single responsibility - update to CALLED status
     */
    private void markEntryAsCalled(WaitlistEntry entry) {
        entry.setStatus(WaitlistStatus.CALLED);
        entry.setCalledAt(LocalDateTime.now());
    }

    /**
     * Update priority of entry
     * SRP: Single responsibility - update priority
     */
    private void updatePriority(WaitlistEntry entry, WaitlistPriority priority) {
        entry.setPriority(priority);
    }

    /**
     * Mark entry as SERVED and set timestamp
     * SRP: Single responsibility - update to SERVED status
     */
    private void markEntryAsServed(WaitlistEntry entry) {
        entry.setStatus(WaitlistStatus.SERVED);
        entry.setServedAt(LocalDateTime.now());
    }

    /**
     * Mark entry as NO_SHOW
     * SRP: Single responsibility - update to NO_SHOW status
     */
    private void markEntryAsNoShow(WaitlistEntry entry) {
        entry.setStatus(WaitlistStatus.NO_SHOW);
    }

    /**
     * Mark entry as CANCELLED and set soft delete timestamp
     * SRP: Single responsibility - update to CANCELLED status
     */
    private void markEntryAsCancelled(WaitlistEntry entry) {
        entry.setStatus(WaitlistStatus.CANCELLED);
        entry.setDeletedAt(LocalDateTime.now());
    }
}