package com.pulseclinic.pulse_server.modules.admissions.service.impl;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.mappers.impl.AdmissionMapper;
import com.pulseclinic.pulse_server.mappers.impl.DoctorMapper;
import com.pulseclinic.pulse_server.mappers.impl.PatientMapper;
import com.pulseclinic.pulse_server.mappers.impl.RoomMapper;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import com.pulseclinic.pulse_server.modules.admissions.repository.AdmissionRepository;
import com.pulseclinic.pulse_server.modules.admissions.service.AdmissionService;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdmissionServiceImpl implements AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;
    private final EncounterRepository encounterRepository;
    private final AdmissionMapper admissionMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final RoomMapper roomMapper;

    public AdmissionServiceImpl(AdmissionRepository admissionRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               RoomRepository roomRepository,
                               EncounterRepository encounterRepository,
                               AdmissionMapper admissionMapper,
                               PatientMapper patientMapper,
                               DoctorMapper doctorMapper,
                               RoomMapper roomMapper) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.roomRepository = roomRepository;
        this.encounterRepository = encounterRepository;
        this.admissionMapper = admissionMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    @Transactional
    public AdmissionDto admitPatient(AdmissionRequestDto admissionRequestDto) {
        Optional<Patient> patientOpt = patientRepository.findById(admissionRequestDto.getPatient_dto().getId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Optional<Admission> existingAdmission = admissionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(
                admissionRequestDto.getPatient_dto().getId(), AdmissionStatus.ONGOING);
        if (existingAdmission.isPresent()) {
            throw new RuntimeException("Patient already has an ongoing admission");
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(admissionRequestDto.getDoctor_dto().getId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Optional<Room> roomOpt = roomRepository.findById(admissionRequestDto.getRoom_dto().getId());
        if (roomOpt.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        Admission admission = Admission.builder()
                .notes(admissionRequestDto.getNotes())
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .room(roomOpt.get())
                .build();

        if (admissionRequestDto.getEncounter_dto().getId() != null) {
            Optional<Encounter> encounterOpt = encounterRepository.findById(admissionRequestDto.getEncounter_dto().getId());
            encounterOpt.ifPresent(admission::setEncounter);
        }

        Admission savedAdmission = admissionRepository.save(admission);
        return admissionMapper.mapTo(savedAdmission);
    }

    @Override
    @Transactional
    public boolean transferRoom(UUID admissionId, UUID newRoomId) {
        try {
            if (!canTransfer(admissionId)) {
                return false;
            }

            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Optional<Room> newRoomOpt = roomRepository.findById(newRoomId);
            if (newRoomOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            admission.setStatus(AdmissionStatus.TRANSFERRED);
            admission.setRoom(newRoomOpt.get());
            admissionRepository.save(admission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateNotes(UUID admissionId, String notes) {
        try {
            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            String existingNotes = admission.getNotes() != null ? admission.getNotes() : "";
            admission.setNotes(existingNotes + "\n" + notes);
            admissionRepository.save(admission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateStatus(UUID admissionId, AdmissionStatus status) {
        try {
            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            admission.setStatus(status);
            admissionRepository.save(admission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean dischargePatient(UUID admissionId) {
        try {
            if (!canDischarge(admissionId)) {
                return false;
            }

            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            admission.setStatus(AdmissionStatus.DISCHARGED);
            admission.setDischarged_at(LocalDateTime.now());
            admissionRepository.save(admission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Duration getDuration(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        if (admissionOpt.isEmpty()) {
            return Duration.ZERO;
        }

        Admission admission = admissionOpt.get();
        LocalDateTime endTime = admission.getDischarged_at() != null ? 
                admission.getDischarged_at() : LocalDateTime.now();
        
        return Duration.between(admission.getAdmitted_at(), endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientDto> getPatient(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        if (admissionOpt.isEmpty()) {
            return Optional.empty();
        }

        Admission admission = admissionOpt.get();
        return Optional.of(patientMapper.mapTo(admission.getPatient()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorDto> getDoctor(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        if (admissionOpt.isEmpty()) {
            return Optional.empty();
        }

        Admission admission = admissionOpt.get();
        return Optional.of(doctorMapper.mapTo(admission.getDoctor()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDto> getRoom(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        if (admissionOpt.isEmpty()) {
            return Optional.empty();
        }

        Admission admission = admissionOpt.get();
        return Optional.of(roomMapper.mapTo(admission.getRoom()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOngoing(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        if (admissionOpt.isEmpty()) {
            return false;
        }

        Admission admission = admissionOpt.get();
        return admission.getStatus() == AdmissionStatus.ONGOING;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canTransfer(UUID admissionId) {
        return isOngoing(admissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDischarge(UUID admissionId) {
        return isOngoing(admissionId);
    }

    @Override
    @Transactional
    public boolean deleteAdmission(UUID admissionId) {
        try {
            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            admission.setDeleted_at(LocalDateTime.now());
            admissionRepository.save(admission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
