package com.pulseclinic.pulse_server.modules.appointments.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository,
                                 ShiftAssignmentRepository shiftAssignmentRepository,
                                 FollowUpPlanRepository followUpPlanRepository,
                                 AppointmentMapper appointmentMapper,
                                 EncounterRepository encounterRepository,
                                 EncounterMapper encounterMapper,
                                 WaitlistEntryRepository waitlistEntryRepository,
                                 NotificationService notificationService) {
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

        List<Appointment> conflicts = appointmentRepository.findConflicts(
                appointmentRequestDto.getDoctorId(),
                appointmentRequestDto.getPatientId(),
                appointmentRequestDto.getStartsAt());

        if (!conflicts.isEmpty()) {
            // Auto-add to waitlist when conflict detected
            Patient patient = patientOpt.get();
            Doctor doctor = doctorOpt.get();

            WaitlistEntry waitlistEntry = WaitlistEntry.builder()
                .patient(patient)
                .doctor(doctor)
                .dutyDate(appointmentRequestDto.getStartsAt().toLocalDate())
                .notes("Auto-added due to time slot conflict at " + appointmentRequestDto.getStartsAt())
                .priority(WaitlistPriority.NORMAL)
                .status(WaitlistStatus.WAITING)
                .build();

            WaitlistEntry savedEntry = waitlistEntryRepository.save(waitlistEntry);

            // Notify patient they've been added to waitlist
            try {
                notificationService.create(NotificationRequestDto.builder()
                    .userId(patient.getUser().getId())
                    .type(NotificationType.APPOINTMENT)
                    .channel(NotificationChannel.EMAIL)
                    .title("Added to Waitlist")
                    .content("The doctor is fully booked at your requested time. You have been added to the waitlist (Ticket #" +
                             savedEntry.getTicketNo() + "). We will notify you when a slot becomes available.")
                    .build());
            } catch (Exception e) {
                // Log notification failure but don't fail the waitlist addition
                System.err.println("Failed to send waitlist notification: " + e.getMessage());
            }

            throw new RuntimeException("Time slot conflict detected. Patient added to waitlist with ticket #" + savedEntry.getTicketNo());
        }

        Appointment appointment = Appointment.builder()
                .startsAt(appointmentRequestDto.getStartsAt())
                .endsAt(appointmentRequestDto.getEndsAt())
                .status(appointmentRequestDto.getStatus())
                .type(appointmentRequestDto.getType())
                .description(appointmentRequestDto.getDescription())
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .build();

        if (appointmentRequestDto.getShiftAssignmentId() != null) {
            Optional<ShiftAssignment> shiftAssignmentOpt = shiftAssignmentRepository.findById(appointmentRequestDto.getShiftAssignmentId());
            shiftAssignmentOpt.ifPresent(appointment::setShiftAssignment);
        }

        if (appointmentRequestDto.getFollowUpPlanId() != null) {
            Optional<FollowUpPlan> followUpPlanOpt = followUpPlanRepository.findById(appointmentRequestDto.getFollowUpPlanId());
            followUpPlanOpt.ifPresent(appointment::setFollowUpPlan);
        }

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
            return null;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED && 
            appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
            return null;
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
