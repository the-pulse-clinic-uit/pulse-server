package com.pulseclinic.pulse_server.modules.encounters.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.AppointmentType;
import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.exception.AppException;
import com.pulseclinic.pulse_server.exception.ErrorCode;
import com.pulseclinic.pulse_server.mappers.impl.AppointmentMapper;
import com.pulseclinic.pulse_server.mappers.impl.FollowUpPlanMapper;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.encounters.repository.FollowUpPlanRepository;
import com.pulseclinic.pulse_server.modules.encounters.service.FollowUpPlanService;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;

@Service
public class FollowUpPlanServiceImpl implements FollowUpPlanService {

    private final FollowUpPlanRepository followUpPlanRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EncounterRepository encounterRepository;
    private final AppointmentRepository appointmentRepository;
    private final FollowUpPlanMapper followUpPlanMapper;
    private final AppointmentMapper appointmentMapper;

    public FollowUpPlanServiceImpl(FollowUpPlanRepository followUpPlanRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  EncounterRepository encounterRepository,
                                  AppointmentRepository appointmentRepository,
                                  FollowUpPlanMapper followUpPlanMapper,
                                  AppointmentMapper appointmentMapper) {
        this.followUpPlanRepository = followUpPlanRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.encounterRepository = encounterRepository;
        this.appointmentRepository = appointmentRepository;
        this.followUpPlanMapper = followUpPlanMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional
    public FollowUpPlanDto createPlan(FollowUpPlanRequestDto followUpPlanRequestDto) {

        validateFollowUpPlanRequest(followUpPlanRequestDto);
        
        Optional<Patient> patientOpt = patientRepository.findById(followUpPlanRequestDto.getPatientId());
        Optional<Doctor> doctorOpt = doctorRepository.findById(followUpPlanRequestDto.getDoctorId());
        Optional<Encounter> encounterOpt = encounterRepository.findById(followUpPlanRequestDto.getBaseEncounterId());

        validation(followUpPlanRequestDto, patientOpt, doctorOpt, encounterOpt);

        FollowUpPlan followUpPlan = FollowUpPlan.builder()
                .firstDueAt(followUpPlanRequestDto.getFirstDueAt())
                .rrule(followUpPlanRequestDto.getRrule())
                .notes(followUpPlanRequestDto.getNotes())
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .status(followUpPlanRequestDto.getStatus() != null ? followUpPlanRequestDto.getStatus() : FollowUpPlanStatus.ACTIVE)
                .baseEncounter(encounterOpt.get())
                .build();

        FollowUpPlan savedPlan = followUpPlanRepository.save(followUpPlan);
        return followUpPlanMapper.mapTo(savedPlan);
    }

    @Override
    @Transactional
    public FollowUpPlanDto createFromEncounter(UUID encounterId, FollowUpPlanRequestDto followUpPlanRequestDto) {

        validateFollowUpPlanRequest(followUpPlanRequestDto);
        
        Optional<Patient> patientOpt = patientRepository.findById(followUpPlanRequestDto.getPatientId());
        Optional<Doctor> doctorOpt = doctorRepository.findById(followUpPlanRequestDto.getDoctorId());
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);

        validation(followUpPlanRequestDto, patientOpt, doctorOpt, encounterOpt);

        Encounter encounter = encounterOpt.get();

        FollowUpPlan followUpPlan = FollowUpPlan.builder()
                .firstDueAt(followUpPlanRequestDto.getFirstDueAt())
                .rrule(followUpPlanRequestDto.getRrule())
                .notes(followUpPlanRequestDto.getNotes())
                .patient(encounter.getPatient())
                .status(followUpPlanRequestDto.getStatus() != null ? followUpPlanRequestDto.getStatus() : FollowUpPlanStatus.ACTIVE)
                .doctor(encounter.getDoctor())
                .baseEncounter(encounter)
                .build();

        FollowUpPlan savedPlan = followUpPlanRepository.save(followUpPlan);
        return followUpPlanMapper.mapTo(savedPlan);
    }

    @Override
    public Optional<FollowUpPlanDto> getFollowUpPlanById(UUID planId) {
        Optional<FollowUpPlan> planOpt = followUpPlanRepository.findById(planId);
        return planOpt.map(followUpPlanMapper::mapTo);
    }

    @Override
    @Transactional
    public boolean editPlan(UUID planId, FollowUpPlanRequestDto followUpPlanRequestDto) {

        validateFollowUpPlanRequest(followUpPlanRequestDto);
        
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));

        if (!canModify(plan)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_COMPLETED_PLAN);
        }

        plan.setFirstDueAt(followUpPlanRequestDto.getFirstDueAt());
        plan.setRrule(followUpPlanRequestDto.getRrule());
        plan.setNotes(followUpPlanRequestDto.getNotes());

        followUpPlanRepository.save(plan);
        return true;
    }

    @Override
    @Transactional
    public boolean pausePlan(UUID planId) {
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));
        
        if (plan.getStatus() != FollowUpPlanStatus.ACTIVE) {
            throw new AppException(ErrorCode.PLAN_NOT_ACTIVE);
        }
        
        plan.setStatus(FollowUpPlanStatus.PAUSED);
        followUpPlanRepository.save(plan);
        return true;
    }

    @Override
    @Transactional
    public boolean resumePlan(UUID planId) {
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));
        
        if (plan.getStatus() != FollowUpPlanStatus.PAUSED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Plan must be paused to resume");
        }
        
        plan.setStatus(FollowUpPlanStatus.ACTIVE);
        followUpPlanRepository.save(plan);
        return true;
    }

    @Override
    @Transactional
    public boolean completePlan(UUID planId) {
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));
        
        if (plan.getStatus() == FollowUpPlanStatus.COMPLETED) {
            throw new AppException(ErrorCode.PLAN_ALREADY_COMPLETED);
        }
        
        plan.setStatus(FollowUpPlanStatus.COMPLETED);
        followUpPlanRepository.save(plan);
        return true;
    }

    @Override
    @Transactional
    public boolean deletePlan(UUID planId) {
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));
        

        plan.setDeletedAt(LocalDateTime.now());
        followUpPlanRepository.save(plan);
        return true;
    }

    @Override
    @Transactional
    public List<AppointmentDto> generateAppointments(UUID planId) {
        FollowUpPlan plan = followUpPlanRepository.findById(planId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOWUP_PLAN_NOT_FOUND));

        if (plan.getStatus() != FollowUpPlanStatus.ACTIVE) {
            throw new AppException(ErrorCode.PLAN_NOT_ACTIVE, 
                "Cannot generate appointments for non-active plan");
        }

        List<LocalDateTime> occurrences = parseRRule(plan.getRrule(), plan.getFirstDueAt());

        List<Appointment> appointments = new ArrayList<>();
        for (LocalDateTime dateTime : occurrences) {

            boolean exists = appointmentRepository.existsByFollowUpPlanAndStartsAt(plan, dateTime);
            if (!exists) {
                Appointment appointment = Appointment.builder()
                        .startsAt(dateTime)
                        .endsAt(dateTime.plusMinutes(30))
                        .status(AppointmentStatus.PENDING)
                        .type(AppointmentType.FOLLOW_UP)
                        .description("Follow-up appointment from plan: " + (plan.getNotes() != null ? plan.getNotes() : ""))
                        .patient(plan.getPatient())
                        .doctor(plan.getDoctor())
                        .followUpPlan(plan)
                        .build();
                
                appointments.add(appointmentRepository.save(appointment));
            }
        }
        
        return appointments.stream()
                .map(appointmentMapper::mapTo)
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> parseRRule(String rruleString, LocalDateTime startDate) {
        List<LocalDateTime> occurrences = new ArrayList<>();
        
        try {
            String[] parts = rruleString.split(";");
            String freq = null;
            int count = 1;
            int interval = 1;
            
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    
                    switch (key) {
                        case "FREQ":
                            freq = value;
                            break;
                        case "COUNT":
                            count = Integer.parseInt(value);
                            break;
                        case "INTERVAL":
                            interval = Integer.parseInt(value);
                            break;
                    }
                }
            }
            
            if (freq == null) {
                throw new IllegalArgumentException("FREQ is required in RRULE");
            }
            

            LocalDateTime current = startDate;
            for (int i = 0; i < count; i++) {
                occurrences.add(current);
                

                switch (freq.toUpperCase()) {
                    case "DAILY":
                        current = current.plusDays(interval);
                        break;
                    case "WEEKLY":
                        current = current.plusWeeks(interval);
                        break;
                    case "MONTHLY":
                        current = current.plusMonths(interval);
                        break;
                    case "YEARLY":
                        current = current.plusYears(interval);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported FREQ: " + freq);
                }
            }
            
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_RRULE, 
                "Failed to parse RRULE: " + e.getMessage());
        }
        
        return occurrences;
    }
    

    @Override
    @Transactional(readOnly = true)
    public List<FollowUpPlanDto> getByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new AppException(ErrorCode.PATIENT_NOT_FOUND);
        }
        
        return followUpPlanRepository.findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(patientId)
                .stream()
                .map(followUpPlanMapper::mapTo)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FollowUpPlanDto> getByDoctor(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new AppException(ErrorCode.DOCTOR_NOT_FOUND);
        }
        
        return followUpPlanRepository.findByDoctorIdAndDeletedAtIsNullOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(followUpPlanMapper::mapTo)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FollowUpPlanDto> getByPatientAndStatus(UUID patientId, FollowUpPlanStatus status) {
        if (!patientRepository.existsById(patientId)) {
            throw new AppException(ErrorCode.PATIENT_NOT_FOUND);
        }
        
        return followUpPlanRepository.findByPatientIdAndStatusAndDeletedAtIsNull(patientId, status)
                .stream()
                .map(followUpPlanMapper::mapTo)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FollowUpPlanDto> getUpcomingPlans(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        return followUpPlanRepository.findAll().stream()
                .filter(plan -> plan.getDeletedAt() == null)
                .filter(plan -> plan.getStatus() == FollowUpPlanStatus.ACTIVE)
                .filter(plan -> {
                    LocalDateTime firstDue = plan.getFirstDueAt();
                    return !firstDue.isBefore(start) && !firstDue.isAfter(end);
                })
                .map(followUpPlanMapper::mapTo)
                .collect(Collectors.toList());
    }

    private boolean canModify(FollowUpPlan plan) {
        return plan.getStatus() == FollowUpPlanStatus.ACTIVE ||
               plan.getStatus() == FollowUpPlanStatus.PAUSED;
    }

    private void validateFollowUpPlanRequest(FollowUpPlanRequestDto dto) {

        if (dto.getFirstDueAt() != null && dto.getFirstDueAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, 
                "First due date must be in the future");
        }
        

        if (dto.getRrule() != null && !dto.getRrule().isEmpty()) {
            try {

                if (!dto.getRrule().contains("FREQ=")) {
                    throw new IllegalArgumentException("RRULE must contain FREQ parameter");
                }
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_RRULE, 
                    "Invalid RRULE format: " + e.getMessage());
            }
        }
    }

    private void validation(FollowUpPlanRequestDto followUpPlanRequestDto,
                            Optional<Patient> patientOpt,
                            Optional<Doctor> doctorOpt,
                            Optional<Encounter> encounterOpt) {
        if (patientOpt.isEmpty()) {
            throw new AppException(ErrorCode.PATIENT_NOT_FOUND);
        }

        if (doctorOpt.isEmpty()) {
            throw new AppException(ErrorCode.DOCTOR_NOT_FOUND);
        }

        if (encounterOpt.isEmpty()) {
            throw new AppException(ErrorCode.ENCOUNTER_NOT_FOUND);
        }


        if (followUpPlanRepository.existsByBaseEncounterId(followUpPlanRequestDto.getBaseEncounterId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, 
                "Follow-up plan already exists for this encounter");
        }
        

        checkPatientReliability(patientOpt.get().getId());
    }

    private void checkPatientReliability(UUID patientId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        

        long noShowCount = appointmentRepository
                .findByPatientIdAndStatusAndStartsAtAfter(
                    patientId, 
                    AppointmentStatus.NO_SHOW, 
                    sixMonthsAgo
                ).size();
        
        if (noShowCount >= 3) {
            throw new AppException(ErrorCode.INVALID_REQUEST, 
                "Patient has " + noShowCount + " no-show appointments in the last 6 months. " +
                "Please confirm attendance before creating new follow-up plan.");
        }
    }
}
