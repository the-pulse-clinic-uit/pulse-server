package com.pulseclinic.pulse_server.modules.encounters.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.mappers.impl.FollowUpPlanMapper;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
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

import javax.print.Doc;

@Service
public class FollowUpPlanServiceImpl implements FollowUpPlanService {

    private final FollowUpPlanRepository followUpPlanRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EncounterRepository encounterRepository;
    private final FollowUpPlanMapper followUpPlanMapper;

    public FollowUpPlanServiceImpl(FollowUpPlanRepository followUpPlanRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  EncounterRepository encounterRepository,
                                  FollowUpPlanMapper followUpPlanMapper) {
        this.followUpPlanRepository = followUpPlanRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.encounterRepository = encounterRepository;
        this.followUpPlanMapper = followUpPlanMapper;
    }

    @Override
    @Transactional
    public FollowUpPlanDto createPlan(FollowUpPlanRequestDto followUpPlanRequestDto) {

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
        try {
            Optional<FollowUpPlan> planOpt = followUpPlanRepository.findById(planId);
            if (planOpt.isEmpty()) {
                return false;
            }

            FollowUpPlan plan = planOpt.get();

            if (!canModify(plan)) {
                return false;
            }

            plan.setFirstDueAt(followUpPlanRequestDto.getFirstDueAt());
            plan.setRrule(followUpPlanRequestDto.getRrule());
            plan.setNotes(followUpPlanRequestDto.getNotes());

            followUpPlanRepository.save(plan);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean pausePlan(UUID planId) {
        try {
            Optional<FollowUpPlan> planOpt = followUpPlanRepository.findById(planId);
            if (planOpt.isEmpty()) {
                return false;
            }

            FollowUpPlan plan = planOpt.get();
            if (plan.getStatus() == FollowUpPlanStatus.ACTIVE) {
                plan.setStatus(FollowUpPlanStatus.PAUSED);
                followUpPlanRepository.save(plan);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resumePlan(UUID planId) {
        try {
            Optional<FollowUpPlan> planOpt = followUpPlanRepository.findById(planId);
            if (planOpt.isEmpty()) {
                return false;
            }

            FollowUpPlan plan = planOpt.get();
            if (plan.getStatus() == FollowUpPlanStatus.PAUSED) {
                plan.setStatus(FollowUpPlanStatus.ACTIVE);
                followUpPlanRepository.save(plan);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean completePlan(UUID planId) {
        try {
            Optional<FollowUpPlan> planOpt = followUpPlanRepository.findById(planId);
            if (planOpt.isEmpty()) {
                return false;
            }

            FollowUpPlan plan = planOpt.get();
            plan.setStatus(FollowUpPlanStatus.COMPLETED);
            followUpPlanRepository.save(plan);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public List<AppointmentDto> generateAppointments(UUID planId) {
        if (!followUpPlanRepository.existsById(planId)) {
            throw new RuntimeException("Follow-up plan not found");
        }

        // TODO: Parse RRULE (RFC 5545) to generate appointment dates
        // This requires an RRULE parser library like ical4j
        // For now, return empty list as placeholder
        
        // Placeholder: Generate 4 weekly appointments starting from first_due_at
        // In production, parse the RRULE string properly
        
        return List.of();
        
        // Example implementation with RRULE library (commented out):
        // List<LocalDateTime> occurrences = RRuleParser.parse(plan.getRrule(), plan.getFirst_due_at());
        // return occurrences.stream()
        //     .map(dateTime -> createAppointment(plan, dateTime))
        //     .map(appointmentMapper::mapTo)
        //     .collect(Collectors.toList());
    }

    private boolean canModify(FollowUpPlan plan) {
        return plan.getStatus() == FollowUpPlanStatus.ACTIVE ||
               plan.getStatus() == FollowUpPlanStatus.PAUSED;
    }

    private void validation(FollowUpPlanRequestDto followUpPlanRequestDto,
                            Optional<Patient> patientOpt,
                            Optional<Doctor> doctorOpt,
                            Optional<Encounter> encounterOpt) {
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Base encounter not found");
        }

        if (followUpPlanRepository.existsByBaseEncounterId(followUpPlanRequestDto.getBaseEncounterId())) {
            throw new RuntimeException("Follow-up plan already exists for this encounter");
        }
    }
}
