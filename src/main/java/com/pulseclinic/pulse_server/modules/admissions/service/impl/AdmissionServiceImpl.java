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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Patient patient;
        Doctor doctor;
        Encounter encounter = null;

        // encounterDto to get patient and doctor
        if (admissionRequestDto.getEncounterDto() != null && admissionRequestDto.getEncounterDto().getId() != null) {
            Optional<Encounter> encounterOpt = encounterRepository.findById(admissionRequestDto.getEncounterDto().getId());
            if (encounterOpt.isEmpty()) {
                throw new RuntimeException("Encounter not found");
            }
            encounter = encounterOpt.get();
            patient = encounter.getPatient();
            doctor = encounter.getDoctor();
        }
        // patientDto and doctorDto directly
        else if (admissionRequestDto.getPatientDto() != null && admissionRequestDto.getDoctorDto() != null) {
            Optional<Patient> patientOpt = patientRepository.findById(admissionRequestDto.getPatientDto().getId());
            if (patientOpt.isEmpty()) {
                throw new RuntimeException("Patient not found");
            }
            patient = patientOpt.get();

            Optional<Doctor> doctorOpt = doctorRepository.findById(admissionRequestDto.getDoctorDto().getId());
            if (doctorOpt.isEmpty()) {
                throw new RuntimeException("Doctor not found");
            }
            doctor = doctorOpt.get();
        } else {
            throw new RuntimeException("Either encounterDto or both patientDto and doctorDto must be provided");
        }

        // Check for existing ongoing admission
        Optional<Admission> existingAdmission = admissionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(
                patient.getId(), AdmissionStatus.ONGOING);
        if (existingAdmission.isPresent()) {
            throw new RuntimeException("Patient already has an ongoing admission");
        }

        // Validate room
        if (admissionRequestDto.getRoomDto() == null || admissionRequestDto.getRoomDto().getId() == null) {
            throw new RuntimeException("Room is required");
        }
        Optional<Room> roomOpt = roomRepository.findById(admissionRequestDto.getRoomDto().getId());
        if (roomOpt.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        Admission admission = Admission.builder()
                .status(admissionRequestDto.getStatus() != null ? admissionRequestDto.getStatus() : AdmissionStatus.ONGOING)
                .notes(admissionRequestDto.getNotes())
                .patient(patient)
                .doctor(doctor)
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

//    @Override
//    @Transactional
//    public boolean transferRoom(UUID admissionId, UUID newRoomId) {
//        try {
//            Optional<Admission> admissionOpt = admissionRepository.findById(admissionId);
//            if (admissionOpt.isEmpty()) {
//                return false;
//            }
//
//            Admission admission = admissionOpt.get();
//            if (!isOngoing(admission)) {
//                return false;
//            }
//
//            Optional<Room> newRoomOpt = roomRepository.findById(newRoomId);
//            if (newRoomOpt.isEmpty()) {
//                return false;
//            }
//
//            admission.setRoom(newRoomOpt.get());
//            admission.setStatus(AdmissionStatus.TRANSFERRED);
//            admissionRepository.save(admission);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

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

    @Override
    public List<AdmissionDto> getAllAdmissions() {
        return this.admissionRepository.findAll().stream().map(admissionMapper::mapTo).collect(Collectors.toList());
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

            Room newRoom = newRoomOpt.get();
            if (!newRoom.getIsAvailable()) {
                return false; // Room not available
            }

            Room oldRoom = admission.getRoom();

            // Update room availability
            oldRoom.setIsAvailable(true);
            newRoom.setIsAvailable(false);
            roomRepository.save(oldRoom);
            roomRepository.save(newRoom);

            // Update admission
            admission.setRoom(newRoom);
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
