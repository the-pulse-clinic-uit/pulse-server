package com.pulseclinic.pulse_server.modules.patients.service.impl;

import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientSearchDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.patients.service.PatientService;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientServiceImpl(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Patient registerPatient(PatientRequestDto patientRequestDto) {
        User user = this.userRepository.findById(patientRequestDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Patient patient = Patient.builder()
                .bloodType(patientRequestDto.getBloodType())
                .allergies(patientRequestDto.getAllergies())
                .healthInsuranceId(patientRequestDto.getHealthInsuranceId())
                .user(user)
                .build();
        return this.patientRepository.save(patient);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return this.patientRepository.findById(id);
    }

    @Override
    public List<Patient> search(PatientSearchDto patientSearchDto) {
        // id
        if (patientSearchDto.getPatientId() != null) {
            return patientRepository.findById(patientSearchDto.getPatientId())
                    .map(List::of)
                    .orElse(List.of());
        }
        // citizen ID
        if (patientSearchDto.getCitizenId() != null && !patientSearchDto.getCitizenId().isEmpty()) {
            return patientRepository.findByUserCitizenId(patientSearchDto.getCitizenId())
                    .map(List::of)
                    .orElse(List.of());
        }
        // full name
        if (patientSearchDto.getFullName() != null && !patientSearchDto.getFullName().isEmpty()) {
            return patientRepository.findByUserFullNameContaining(patientSearchDto.getFullName());
        }
        // email
        if (patientSearchDto.getEmail() != null && !patientSearchDto.getEmail().isEmpty()) {
            return patientRepository.findByUserEmailContaining(patientSearchDto.getEmail());
        }
        return patientRepository.findAll();
    }

    @Override
    public Patient updatePatient(UUID id, PatientDto patientDto) {
        Optional<Patient> patient = patientRepository.findById(id);

        if (patient.isPresent()) {
            Patient updatedPatient = patient.get();
            if (patientDto.getAllergies() != null) {
                updatedPatient.setAllergies(patientDto.getAllergies());
            }
            if (patientDto.getHealthInsuranceId() != null) {
                updatedPatient.setHealthInsuranceId(patientDto.getHealthInsuranceId());
            }
            if (patientDto.getBloodType() != null) {
                updatedPatient.setBloodType(patientDto.getBloodType());
            }
            return patientRepository.save(updatedPatient);
        }
        else throw new RuntimeException("Patient not found");
    }

    @Override
    public Patient updatePatientMe(String email, PatientDto patientDto) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            Optional<Patient> patient = patientRepository.findById(patientDto.getId());
            if (patient.isEmpty()) {
                throw new RuntimeException("Patient not found");
            }
            if (!patient.get().getUser().getId().equals(user.get().getId())) {
                throw new RuntimeException("Unauthorized: Patient does not belong to this user");
            }
            Patient updatedPatient = patient.get();
            if (patientDto.getAllergies() != null) {
                updatedPatient.setAllergies(patientDto.getAllergies());
            }
            if (patientDto.getHealthInsuranceId() != null) {
                updatedPatient.setHealthInsuranceId(patientDto.getHealthInsuranceId());
            }
            if (patientDto.getBloodType() != null) {
                updatedPatient.setBloodType(patientDto.getBloodType());
            }
            return patientRepository.save(updatedPatient);
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        Optional<User> user =  this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return patientRepository.findByUser(user.get());
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public List<Patient> getPatients(){
        return this.patientRepository.findAll();
    }
}
