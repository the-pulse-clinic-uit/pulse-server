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

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository,
                                 ShiftAssignmentRepository shiftAssignmentRepository,
                                 FollowUpPlanRepository followUpPlanRepository,
                                 AppointmentMapper appointmentMapper,
                                 EncounterRepository encounterRepository,
                                 EncounterMapper encounterMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.followUpPlanRepository = followUpPlanRepository;
        this.appointmentMapper = appointmentMapper;
        this.encounterRepository = encounterRepository;
        this.encounterMapper = encounterMapper;
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
            throw new RuntimeException("Time slot conflict detected");
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
            return true;
        } catch (Exception e) {
            return false;
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
}
