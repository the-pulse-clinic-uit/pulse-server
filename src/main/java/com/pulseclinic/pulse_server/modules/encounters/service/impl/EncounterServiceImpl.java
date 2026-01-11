package com.pulseclinic.pulse_server.modules.encounters.service.impl;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.mappers.impl.EncounterMapper;
import com.pulseclinic.pulse_server.mappers.impl.FollowUpPlanMapper;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import com.pulseclinic.pulse_server.modules.admissions.repository.AdmissionRepository;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterSummaryDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterWithAdmissionStatusDto;
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
    private final AdmissionRepository admissionRepository;
    private final EncounterMapper encounterMapper;
    private final FollowUpPlanMapper followUpPlanMapper;

    public EncounterServiceImpl(EncounterRepository encounterRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               AppointmentRepository appointmentRepository,
                               FollowUpPlanRepository followUpPlanRepository,
                               AdmissionRepository admissionRepository,
                               EncounterMapper encounterMapper,
                               FollowUpPlanMapper followUpPlanMapper) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.followUpPlanRepository = followUpPlanRepository;
        this.admissionRepository = admissionRepository;
        this.encounterMapper = encounterMapper;
        this.followUpPlanMapper = followUpPlanMapper;
    }

    @Override
    @Transactional
    public EncounterDto startEncounter(EncounterRequestDto encounterRequestDto) {
        // Tìm bệnh nhân
        Optional<Patient> patientOpt = patientRepository.findById(encounterRequestDto.getPatientId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        // Tìm bác sĩ
        Optional<Doctor> doctorOpt = doctorRepository.findById(encounterRequestDto.getDoctorId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        if (encounterRequestDto.getEndedAt() != null &&
                encounterRequestDto.getEndedAt().isBefore(encounterRequestDto.getStartedAt())) {
            throw new RuntimeException("Ended time must be after started time");
        }

        if (encounterRequestDto.getStartedAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Encounter cannot start in the future");
        }

        if (encounterRequestDto.getAppointmentId() != null &&
                encounterRepository.existsByAppointmentId(encounterRequestDto.getAppointmentId())) {
            throw new RuntimeException("Encounter already exists for this appointment");
        }

        Encounter encounter = Encounter.builder()
                .type(encounterRequestDto.getType())
                .startedAt(encounterRequestDto.getStartedAt() != null ? encounterRequestDto.getStartedAt() : LocalDateTime.now())
                .endedAt(encounterRequestDto.getEndedAt())
                .diagnosis(encounterRequestDto.getDiagnosis() != null ? encounterRequestDto.getDiagnosis() : "")
                .notes(encounterRequestDto.getNotes() != null ? encounterRequestDto.getNotes() : "")
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .build();

        // Liên kết với appointment nếu có
        if (encounterRequestDto.getAppointmentId() != null) {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(encounterRequestDto.getAppointmentId());
            if (appointmentOpt.isPresent()) {
                Appointment appt = appointmentOpt.get();

                if (!appt.getDoctor().getId().equals(encounterRequestDto.getDoctorId())) {
                    throw new RuntimeException("Doctor does not match appointment");
                }

                if (!appt.getPatient().getId().equals(encounterRequestDto.getPatientId())) {
                    throw new RuntimeException("Patient does not match appointment");
                }
            }
        }

        Encounter savedEncounter = encounterRepository.save(encounter);
        return encounterMapper.mapTo(savedEncounter);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EncounterDto> getEncounterById(UUID encounterId) {
        return encounterRepository.findById(encounterId)
                .map(encounterMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getAllEncounters() {
        List<Encounter> encounters = encounterRepository.findByDeletedAtIsNullOrderByStartedAtDesc();
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterSummaryDto> getAllEncounterSummaries() {
        List<Encounter> encounters = encounterRepository.findByDeletedAtIsNullOrderByStartedAtDesc();
        return encounters.stream()
                .map(encounterMapper::mapToSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getEncountersByPatient(UUID patientId) {
        List<Encounter> encounters = encounterRepository.findByPatientIdAndDeletedAtIsNullOrderByStartedAtDesc(patientId);
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getEncountersByDoctor(UUID doctorId) {
        List<Encounter> encounters = encounterRepository.findByDoctorIdAndDeletedAtIsNullOrderByStartedAtDesc(doctorId);
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getEncountersByPatientAndDoctor(UUID patientId, UUID doctorId) {
        List<Encounter> encounters = encounterRepository.findByPatientIdAndDoctorIdAndDeletedAtIsNull(patientId, doctorId);
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getEncountersByType(com.pulseclinic.pulse_server.enums.EncounterType type) {
        List<Encounter> encounters = encounterRepository.findByTypeAndDeletedAtIsNullOrderByStartedAtDesc(type);
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getActiveEncounters() {
        List<Encounter> encounters = encounterRepository.findByEndedAtIsNullAndDeletedAtIsNullOrderByStartedAtDesc();
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getCompletedEncounters() {
        List<Encounter> encounters = encounterRepository.findByEndedAtIsNotNullAndDeletedAtIsNullOrderByEndedAtDesc();
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterWithAdmissionStatusDto> getCompletedEncountersWithAdmissionStatus() {
        List<Encounter> encounters = encounterRepository.findByEndedAtIsNotNullAndDeletedAtIsNullOrderByEndedAtDesc();
        return encounters.stream()
                .map(encounter -> {
                    Optional<Admission> admissionOpt = admissionRepository.findByEncounterIdAndDeletedAtIsNull(encounter.getId());
                    AdmissionStatus admissionStatus = admissionOpt.map(Admission::getStatus).orElse(null);

                    String patientName = null;
                    if (encounter.getPatient() != null && encounter.getPatient().getUser() != null) {
                        patientName = encounter.getPatient().getUser().getFullName();
                    }

                    String doctorName = null;
                    if (encounter.getDoctor() != null && encounter.getDoctor().getStaff() != null
                            && encounter.getDoctor().getStaff().getUser() != null) {
                        doctorName = encounter.getDoctor().getStaff().getUser().getFullName();
                    }

                    return EncounterWithAdmissionStatusDto.builder()
                            .id(encounter.getId())
                            .type(encounter.getType())
                            .startedAt(encounter.getStartedAt())
                            .endedAt(encounter.getEndedAt())
                            .diagnosis(encounter.getDiagnosis())
                            .notes(encounter.getNotes())
                            .createdAt(encounter.getCreatedAt())
                            .patientId(encounter.getPatient() != null ? encounter.getPatient().getId() : null)
                            .patientName(patientName)
                            .doctorId(encounter.getDoctor() != null ? encounter.getDoctor().getId() : null)
                            .doctorName(doctorName)
                            .appointmentId(encounter.getAppointment() != null ? encounter.getAppointment().getId() : null)
                            .admissionStatus(admissionStatus)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterWithAdmissionStatusDto> getEncountersEligibleForAdmission() {
        List<Encounter> encounters = encounterRepository.findByEndedAtIsNotNullAndDeletedAtIsNullOrderByEndedAtDesc();
        return encounters.stream()
                .map(encounter -> {
                    Optional<Admission> admissionOpt = admissionRepository.findByEncounterIdAndDeletedAtIsNull(encounter.getId());
                    AdmissionStatus admissionStatus = admissionOpt.map(Admission::getStatus).orElse(null);
                    return new Object[] { encounter, admissionStatus };
                })
                .filter(arr -> {
                    AdmissionStatus status = (AdmissionStatus) arr[1];
                    return status == null || status == AdmissionStatus.DISCHARGED || status == AdmissionStatus.OUTPATIENT;
                })
                .map(arr -> {
                    Encounter encounter = (Encounter) arr[0];
                    AdmissionStatus admissionStatus = (AdmissionStatus) arr[1];

                    String patientName = null;
                    if (encounter.getPatient() != null && encounter.getPatient().getUser() != null) {
                        patientName = encounter.getPatient().getUser().getFullName();
                    }

                    String doctorName = null;
                    if (encounter.getDoctor() != null && encounter.getDoctor().getStaff() != null
                            && encounter.getDoctor().getStaff().getUser() != null) {
                        doctorName = encounter.getDoctor().getStaff().getUser().getFullName();
                    }

                    return EncounterWithAdmissionStatusDto.builder()
                            .id(encounter.getId())
                            .type(encounter.getType())
                            .startedAt(encounter.getStartedAt())
                            .endedAt(encounter.getEndedAt())
                            .diagnosis(encounter.getDiagnosis())
                            .notes(encounter.getNotes())
                            .createdAt(encounter.getCreatedAt())
                            .patientId(encounter.getPatient() != null ? encounter.getPatient().getId() : null)
                            .patientName(patientName)
                            .doctorId(encounter.getDoctor() != null ? encounter.getDoctor().getId() : null)
                            .doctorName(doctorName)
                            .appointmentId(encounter.getAppointment() != null ? encounter.getAppointment().getId() : null)
                            .admissionStatus(admissionStatus)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getEncountersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Encounter> encounters = encounterRepository.findByDateRange(startDate, endDate);
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getTodayEncounters() {
        List<Encounter> encounters = encounterRepository.findTodayEncounters();
        return encounters.stream()
                .map(encounterMapper::mapTo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EncounterDto> getEncounterByAppointment(UUID appointmentId) {
        return encounterRepository.findByAppointmentIdAndDeletedAtIsNull(appointmentId)
                .map(encounterMapper::mapTo);
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
    @Transactional(readOnly = true)
    public String generateSummary(UUID encounterId) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            return "";
        }

        Encounter encounter = encounterOpt.get();
        StringBuilder summary = new StringBuilder();

        summary.append("=== ENCOUNTER REPORT ===\n\n");
        summary.append("ID: ").append(encounter.getId()).append("\n");
        summary.append("Type: ").append(encounter.getType()).append("\n");
        summary.append("Patient: ").append(encounter.getPatient().getUser().getFullName()).append("\n");
        summary.append("Doctor: ").append(encounter.getDoctor().getStaff().getUser().getFullName()).append("\n");
        summary.append("Started at: ").append(encounter.getStartedAt()).append("\n");

        if (encounter.getEndedAt() != null) {
            summary.append("Ended at: ").append(encounter.getEndedAt()).append("\n");
            summary.append("Duration: ").append(getDuration(encounterId).toMinutes()).append(" minutes\n");
        } else {
            summary.append("Status: In progress\n");
        }

        summary.append("\nDIAGNOSIS:\n").append(encounter.getDiagnosis()).append("\n");
        summary.append("\nNOTES:\n").append(encounter.getNotes()).append("\n");

        return summary.toString();
    }

    private Duration getDuration(UUID encounterId) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            return Duration.ZERO;
        }

        Encounter encounter = encounterOpt.get();
        if (encounter.getEndedAt() == null) {
            return Duration.between(encounter.getStartedAt(), LocalDateTime.now());
        }

        return Duration.between(encounter.getStartedAt(), encounter.getEndedAt());
    }
}