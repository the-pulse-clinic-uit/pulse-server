package com.pulseclinic.pulse_server.modules.appointments.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftRepository;
import com.pulseclinic.pulse_server.modules.scheduling.service.ShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.EncounterType;
import com.pulseclinic.pulse_server.mappers.impl.AppointmentMapper;
import com.pulseclinic.pulse_server.mappers.impl.EncounterMapper;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.appointments.service.AppointmentService;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.encounters.repository.FollowUpPlanRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.scheduling.repository.ShiftAssignmentRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.scheduling.entity.WaitlistEntry;
import com.pulseclinic.pulse_server.modules.scheduling.repository.WaitlistEntryRepository;
import com.pulseclinic.pulse_server.modules.notifications.service.NotificationService;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.enums.NotificationType;
import com.pulseclinic.pulse_server.enums.NotificationChannel;
import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.enums.WaitlistStatus;

import javax.swing.text.html.Option;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final FollowUpPlanRepository followUpPlanRepository;
    private final AppointmentMapper appointmentMapper;
    private final EncounterRepository encounterRepository;
    private final EncounterMapper encounterMapper;
    private final WaitlistEntryRepository waitlistEntryRepository;
    private final NotificationService notificationService;
    private final ShiftService shiftService;
    private final ShiftRepository shiftRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  ShiftAssignmentRepository shiftAssignmentRepository,
                                  FollowUpPlanRepository followUpPlanRepository,
                                  AppointmentMapper appointmentMapper,
                                  EncounterRepository encounterRepository,
                                  EncounterMapper encounterMapper,
                                  WaitlistEntryRepository waitlistEntryRepository,
                                  NotificationService notificationService, ShiftService shiftService,
                                  ShiftRepository shiftRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.followUpPlanRepository = followUpPlanRepository;
        this.appointmentMapper = appointmentMapper;
        this.encounterRepository = encounterRepository;
        this.encounterMapper = encounterMapper;
        this.waitlistEntryRepository = waitlistEntryRepository;
        this.notificationService = notificationService;
        this.shiftService = shiftService;
        this.shiftRepository = shiftRepository;
    }

    @Override
    @Transactional
    public AppointmentDto scheduleAppointment(AppointmentRequestDto appointmentRequestDto) {
        Optional<Patient> patientOpt = patientRepository.findById(appointmentRequestDto.getPatientId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(appointmentRequestDto.getDoctorId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Optional<ShiftAssignment> shiftAssignmentOpt = shiftAssignmentRepository.findById(appointmentRequestDto.getShiftAssignmentId());
        if (shiftAssignmentOpt.isEmpty()) {
            throw new RuntimeException("Shift assignment not found");
        }

        ShiftAssignment shiftAssignment = shiftAssignmentOpt.get();
//        Shift shift = shiftAssignment.getShift();

        long appointmentsInSlot = appointmentRepository.countOccupiedSlots(
                shiftAssignment.getShift().getId(),
                appointmentRequestDto.getStartsAt(),
                appointmentRequestDto.getStartsAt().plusMinutes(shiftAssignment.getShift().getSlotMinutes())
        );

        // 3. Check capacity
        if (appointmentsInSlot >= shiftAssignment.getShift().getCapacityPerSlot()) {
            throw new RuntimeException("This slot is fully booked");
        }

        LocalDateTime startsAt = appointmentRequestDto.getStartsAt();
        LocalDateTime endsAt = appointmentRequestDto.getStartsAt().plusMinutes(shiftAssignment.getShift().getSlotMinutes());


        Appointment appointment = Appointment.builder()
                .startsAt(startsAt)
                .endsAt(endsAt)
                .status(appointmentRequestDto.getStatus())
                .type(appointmentRequestDto.getType())
                .shiftAssignment(shiftAssignment)
                .description(appointmentRequestDto.getDescription())
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.mapTo(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentDto> getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(appointmentMapper::mapTo);
    }

    @Override
    @Transactional
    public boolean rescheduleAppointment(UUID appointmentId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return false;
            }

            Appointment appointment = appointmentOpt.get();

            if (!canReschedule(appointment)) {
                return false;
            }

            List<Appointment> conflicts = appointmentRepository.findConflicts(
                    appointment.getDoctor().getId(),
                    appointment.getPatient().getId(),
                    newStartTime);

            if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(appointmentId)) {
                return false;
            }

            appointment.setStartsAt(newStartTime);
            appointment.setEndsAt(newEndTime);
            appointmentRepository.save(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelAppointment(UUID appointmentId, String reason) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return false;
            }

            Appointment appointment = appointmentOpt.get();

            if (!canCancel(appointment)) {
                return false;
            }

            appointment.setStatus(AppointmentStatus.CANCELLED);
            if (reason != null) {
                appointment.setDescription(appointment.getDescription() + "\nCancellation reason: " + reason);
            }
            appointmentRepository.save(appointment);

            // Auto-notify waitlist when appointment is cancelled
            notifyWaitlistOnCancellation(appointment);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void notifyWaitlistOnCancellation(Appointment appointment) {
        try {
            // Find people waiting for this doctor on this date
            List<WaitlistEntry> waitingList = waitlistEntryRepository.findByDoctorIdAndDutyDateAndStatus(
                    appointment.getDoctor().getId(),
                    appointment.getStartsAt().toLocalDate(),
                    WaitlistStatus.WAITING
            );

            if (!waitingList.isEmpty()) {
                // Get the first person in the queue (sorted by priority and creation time)
                WaitlistEntry nextPerson = waitingList.stream()
                        .sorted((a, b) -> {
                            // First compare by priority (higher priority first)
                            int priorityCompare = b.getPriority().compareTo(a.getPriority());
                            if (priorityCompare != 0) return priorityCompare;
                            // Then by creation time (earlier first)
                            return a.getCreatedAt().compareTo(b.getCreatedAt());
                        })
                        .findFirst()
                        .get();

                // Update status to CALLED
                nextPerson.setStatus(WaitlistStatus.CALLED);
                nextPerson.setCalledAt(LocalDateTime.now());
                waitlistEntryRepository.save(nextPerson);

                // Send notification
                try {
                    notificationService.create(NotificationRequestDto.builder()
                            .userId(nextPerson.getPatient().getUser().getId())
                            .type(NotificationType.APPOINTMENT)
                            .channel(NotificationChannel.EMAIL)
                            .title("Appointment Slot Available")
                            .content("Good news! A time slot is now available with Dr. " +
                                    appointment.getDoctor().getStaff().getUser().getFullName() +
                                    " on " + appointment.getStartsAt().toLocalDate() +
                                    " at " + appointment.getStartsAt().toLocalTime() +
                                    ". Please contact us to confirm your appointment. Your ticket number: #" +
                                    nextPerson.getTicketNo())
                            .build());
                } catch (Exception e) {
                    System.err.println("Failed to send slot available notification: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the cancellation
            System.err.println("Failed to process waitlist notification: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean confirmAppointment(UUID appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public boolean checkIn(UUID appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.CHECKED_IN);
    }

    @Override
    @Transactional
    public boolean markAsDone(UUID appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.DONE);
    }

    @Override
    @Transactional
    public EncounterDto createEncounter(UUID appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found with id: " + appointmentId);
        }

        Appointment appointment = appointmentOpt.get();

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED &&
                appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
            throw new RuntimeException("Appointment must be CONFIRMED or CHECKED_IN to create encounter. Current status: " + appointment.getStatus());
        }

        // Auto check-in if not already checked in
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            appointment.setStatus(AppointmentStatus.CHECKED_IN);
            appointmentRepository.save(appointment);
        }

        Encounter encounter = Encounter.builder()
                .type(EncounterType.APPOINTED)
                .startedAt(LocalDateTime.now())
                .diagnosis("")
                .notes("Encounter created from appointment")
                .patient(appointment.getPatient())
                .doctor(appointment.getDoctor())
                .appointment(appointment)
                .build();

        Encounter savedEncounter = encounterRepository.save(encounter);
        return encounterMapper.mapTo(savedEncounter);
    }

    private boolean updateStatus(UUID appointmentId, AppointmentStatus newStatus) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return false;
            }


            Appointment appointment = appointmentOpt.get();

            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                throw new IllegalStateException("Cannot update a cancelled appointment");
            }

            appointment.setStatus(newStatus);
            appointmentRepository.save(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean canReschedule(Appointment appointment) {
        AppointmentStatus status = appointment.getStatus();
        return status == AppointmentStatus.PENDING ||
                status == AppointmentStatus.CONFIRMED;
    }

    private boolean canCancel(Appointment appointment) {
        AppointmentStatus status = appointment.getStatus();
        return status != AppointmentStatus.CANCELLED &&
                status != AppointmentStatus.DONE &&
                status != AppointmentStatus.NO_SHOW;
    }

    // New query method implementations
    @Override
    public List<AppointmentDto> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findByDeletedAtIsNullOrderByStartsAtDesc();
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByStatusAndDeletedAtIsNullOrderByStartsAtAsc(status);
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDoctor(UUID doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDeletedAtIsNullOrderByStartsAtDesc(doctorId);
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatient(UUID patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDeletedAtIsNullOrderByStartsAtDesc(patientId);
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentRepository.findByDateRange(startDate, endDate);
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }

    @Override
    public List<AppointmentDto> getPendingAppointments() {
        return getAppointmentsByStatus(AppointmentStatus.PENDING);
    }

    @Override
    public List<AppointmentDto> getConfirmedAppointments() {
        return getAppointmentsByStatus(AppointmentStatus.CONFIRMED);
    }

    @Override
    public List<AppointmentDto> getTodayAppointments() {
        List<Appointment> appointments = appointmentRepository.findTodayAppointments();
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .toList();
    }
}
