package com.pulseclinic.pulse_server.modules.admissions.service.impl;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.mappers.impl.AdmissionMapper;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.admissions.entity.Admission;
import com.pulseclinic.pulse_server.modules.admissions.repository.AdmissionRepository;
import com.pulseclinic.pulse_server.modules.admissions.service.AdmissionService;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.encounters.repository.EncounterRepository;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AdmissionServiceImpl(AdmissionRepository admissionRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               RoomRepository roomRepository,
                               EncounterRepository encounterRepository,
                               AdmissionMapper admissionMapper) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.roomRepository = roomRepository;
        this.encounterRepository = encounterRepository;
        this.admissionMapper = admissionMapper;
    }

    @Override
    @Transactional
    public AdmissionDto admitPatient(AdmissionRequestDto admissionRequestDto) {
        UUID patientId = admissionRequestDto.getPatient_dto().getId();
        UUID doctorId = admissionRequestDto.getDoctor_dto().getId();
        UUID roomId = admissionRequestDto.getRoom_dto().getId();

        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Optional<Admission> existingAdmission = admissionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(
                patientId, AdmissionStatus.ONGOING);
        if (existingAdmission.isPresent()) {
            throw new RuntimeException("Patient already has an ongoing admission");
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        Admission admission = Admission.builder()
                .notes(admissionRequestDto.getNotes())
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .room(roomOpt.get())
                .build();

        if (admissionRequestDto.getEncounter_dto() != null && 
            admissionRequestDto.getEncounter_dto().getId() != null) {
            Optional<Encounter> encounterOpt = encounterRepository.findById(
                admissionRequestDto.getEncounter_dto().getId());
            encounterOpt.ifPresent(admission::setEncounter);
        }

        Admission savedAdmission = admissionRepository.save(admission);
        return admissionMapper.mapTo(savedAdmission);
    }

    @Override
    @Transactional
    public AdmissionDto createFromEncounter(UUID encounterId, AdmissionRequestDto admissionRequestDto) {
        Optional<Encounter> encounterOpt = encounterRepository.findById(encounterId);
        if (encounterOpt.isEmpty()) {
            throw new RuntimeException("Encounter not found");
        }

        Encounter encounter = encounterOpt.get();
        Patient patient = encounter.getPatient();
        UUID roomId = admissionRequestDto.getRoom_dto().getId();

        Optional<Admission> existingAdmission = admissionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(
                patient.getId(), AdmissionStatus.ONGOING);
        if (existingAdmission.isPresent()) {
            throw new RuntimeException("Patient already has an ongoing admission");
        }

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        Admission admission = Admission.builder()
                .notes(admissionRequestDto.getNotes())
                .patient(patient)
                .doctor(encounter.getDoctor())
                .room(roomOpt.get())
                .encounter(encounter)
                .build();

        Admission savedAdmission = admissionRepository.save(admission);
        return admissionMapper.mapTo(savedAdmission);
    }

    @Override
    public Optional<AdmissionDto> getAdmissionById(UUID admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
        return admissionOpt.map(admissionMapper::mapTo);
    }

    @Override
    @Transactional
    public boolean transferRoom(UUID admissionId, UUID newRoomId) {
        try {
            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            if (!isOngoing(admission)) {
                return false;
            }

            Optional<Room> newRoomOpt = roomRepository.findById(newRoomId);
            if (newRoomOpt.isEmpty()) {
                return false;
            }

            admission.setRoom(newRoomOpt.get());
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
            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
            if (admissionOpt.isEmpty()) {
                return false;
            }

            Admission admission = admissionOpt.get();
            if (!isOngoing(admission)) {
                return false;
            }

            admission.setStatus(AdmissionStatus.DISCHARGED);
            admission.setDischargedAt(LocalDateTime.now());
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

    private boolean isOngoing(Admission admission) {
        return admission.getStatus() == AdmissionStatus.ONGOING;
    }
}
