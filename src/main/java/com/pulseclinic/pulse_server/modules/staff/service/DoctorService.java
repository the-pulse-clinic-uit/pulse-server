package com.pulseclinic.pulse_server.modules.staff.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorRequestDto;

public interface DoctorService {
    DoctorDto createDoctor(DoctorRequestDto doctorRequestDto);
    List<DoctorDto> getAllDoctors();
    Optional<DoctorDto> getDoctorById(UUID doctorId);
    Optional<DoctorDto> findByEmail(String email);
    DoctorDto updateDoctor(UUID doctorId, DoctorRequestDto doctorRequestDto);
    boolean updateSpecialization(UUID doctorId, UUID departmentId);
    List<Object> getPatients(UUID doctorId);
    List<Object> getShiftSchedule(UUID doctorId, LocalDate date);
    boolean checkAvailability(UUID doctorId, LocalDateTime dateTime);
}
