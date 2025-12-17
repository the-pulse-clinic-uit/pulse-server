package com.pulseclinic.pulse_server.modules.encounters.service.impl;

import com.pulseclinic.pulse_server.mappers.impl.EncounterMapper;
import com.pulseclinic.pulse_server.mappers.impl.FollowUpPlanMapper;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.encounters.repository.FollowUpPlanRepository;
import com.pulseclinic.pulse_server.modules.encounters.service.EncounterService;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EncounterServiceImpl implements EncounterService {

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final FollowUpPlanRepository followUpPlanRepository;
    private final EncounterMapper encounterMapper;
    private final FollowUpPlanMapper followUpPlanMapper;

    public EncounterServiceImpl(EncounterRepository encounterRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               AppointmentRepository appointmentRepository,
                               FollowUpPlanRepository followUpPlanRepository,
                               EncounterMapper encounterMapper,
                               FollowUpPlanMapper followUpPlanMapper) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.followUpPlanRepository = followUpPlanRepository;
        this.encounterMapper = encounterMapper;
        this.followUpPlanMapper = followUpPlanMapper;
    }

    @Override
    @Transactional
    public EncounterDto startEncounter(EncounterRequestDto encounterRequestDto) {
        // Tìm bệnh nhân
        Optional<Patient> patientOpt = patientRepository.findById(encounterRequestDto.getPatient_id());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        // Tìm bác sĩ
        Optional<Doctor> doctorOpt = doctorRepository.findById(encounterRequestDto.getDoctor_id());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Encounter encounter = Encounter.builder()
                .type(encounterRequestDto.getType())
                .startedAt(LocalDateTime.now())
                .diagnosis(encounterRequestDto.getDiagnosis() != null ? encounterRequestDto.getDiagnosis() : "")
                .notes(encounterRequestDto.getNotes() != null ? encounterRequestDto.getNotes() : "")
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .build();

        // Liên kết với appointment nếu có
        if (encounterRequestDto.getAppointment_id() != null) {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(encounterRequestDto.getAppointment_id());
            appointmentOpt.ifPresent(encounter::setAppointment);
        }

        Encounter savedEncounter = encounterRepository.save(encounter);
        return encounterMapper.mapTo(savedEncounter);
    }

    @Override
    @Transactional
    public boolean recordDiagnosis(UUID encounterId, String diagnosis) {
        try {
            Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
            if (encounterOpt.isEmpty()) {
                return false;
            }

            Encounter encounter = encounterOpt.get();
            encounter.setDiagnosis(diagnosis);
            encounterRepository.save(encounter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addNotes(UUID encounterId, String notes) {
        try {
            Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
            if (encounterOpt.isEmpty()) {
                return false;
            }

            Encounter encounter = encounterOpt.get();
            String existingNotes = encounter.getNotes();
            encounter.setNotes(existingNotes + "\n" + notes);
            encounterRepository.save(encounter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean endEncounter(UUID encounterId) {
        try {
            Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
            if (encounterOpt.isEmpty()) {
                return false;
            }

            Encounter encounter = encounterOpt.get();
            encounter.setEndedAt(LocalDateTime.now());
            encounterRepository.save(encounter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Object createInvoice(UUID encounterId) {
        // TODO: Implement với module Invoice (Billing)
        return null;
    }

    @Override
    @Transactional
    public Object createPrescription(UUID encounterId) {
        // TODO: Implement với module Prescription (Pharmacy)
        return null;
    }

    @Override
    @Transactional
    public Object admitPatient(UUID encounterId, UUID roomId) {
        // TODO: Implement với module Admission
        return null;
    }

    @Override
    @Transactional
    public FollowUpPlanDto createFollowUpPlan(UUID encounterId, String rrule, String notes) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Encounter not found");
        }

        Encounter encounter = encounterOpt.get();

        FollowUpPlan followUpPlan = FollowUpPlan.builder()
                .firstDueAt(LocalDateTime.now().plusDays(7)) // Mặc định tái khám sau 7 ngày
                .rrule(rrule)
                .notes(notes)
                .patient(encounter.getPatient())
                .doctor(encounter.getDoctor())
                .baseEncounter(encounter)
                .build();

        FollowUpPlan savedPlan = followUpPlanRepository.save(followUpPlan);
        return followUpPlanMapper.mapTo(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Duration getDuration(UUID encounterId) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            return Duration.ZERO;
        }

        Encounter encounter = encounterOpt.get();
        if (encounter.getEndedAt() == null) {
            // Nếu chưa kết thúc, tính từ lúc bắt đầu đến hiện tại
            return Duration.between(encounter.getStartedAt(), LocalDateTime.now());
        }

        return Duration.between(encounter.getStartedAt(), encounter.getEndedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isComplete(UUID encounterId) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            return false;
        }

        Encounter encounter = encounterOpt.get();
        return encounter.getEndedAt() != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getPrescriptions(UUID encounterId) {
        // TODO: Implement với module Prescription (Pharmacy)
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getInvoices(UUID encounterId) {
        // TODO: Implement với module Invoice (Billing)
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Object> getAdmission(UUID encounterId) {
        // TODO: Implement với module Admission
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public String generateSummary(UUID encounterId) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            return "";
        }

        Encounter encounter = encounterOpt.get();
        StringBuilder summary = new StringBuilder();
        
        summary.append("=== BÁO CÁO KHÁM BỆNH ===\n\n");
        summary.append("Mã số: ").append(encounter.getId()).append("\n");
        summary.append("Loại khám: ").append(encounter.getType()).append("\n");
        summary.append("Bệnh nhân: ").append(encounter.getPatient().getUser().getFullName()).append("\n");
        summary.append("Bác sĩ: ").append(encounter.getDoctor().getStaff().getUser().getFullName()).append("\n");
        summary.append("Thời gian bắt đầu: ").append(encounter.getStartedAt()).append("\n");

        if (encounter.getEndedAt() != null) {
            summary.append("Thời gian kết thúc: ").append(encounter.getEndedAt()).append("\n");
            summary.append("Thời lượng: ").append(getDuration(encounterId).toMinutes()).append(" phút\n");
        } else {
            summary.append("Trạng thái: Đang khám\n");
        }
        
        summary.append("\nCHẨN ĐOÁN:\n").append(encounter.getDiagnosis()).append("\n");
        summary.append("\nGHI CHÚ:\n").append(encounter.getNotes()).append("\n");
        
        return summary.toString();
    }
}
