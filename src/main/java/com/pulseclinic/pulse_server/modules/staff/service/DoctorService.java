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
    boolean updateSpecialization(UUID doctorId, UUID departmentId);
    boolean verifyLicense(UUID doctorId);
    boolean checkLicenseValidity(UUID doctorId);
    List<Object> getAppointments(UUID doctorId, LocalDate date);
    List<Object> getUpcomingAppointments(UUID doctorId);
    List<Object> getEncounters(UUID doctorId, LocalDate startDate, LocalDate endDate);
    Object prescribeMedication(UUID doctorId, UUID encounterId);
    boolean recordDiagnosis(UUID doctorId, UUID encounterId, String diagnosis);
    Object createFollowUpPlan(UUID doctorId, UUID encounterId);
    List<Object> getShiftSchedule(UUID doctorId, LocalDate date);
    boolean checkAvailability(UUID doctorId, LocalDateTime dateTime);
    Optional<Object> getDepartment(UUID doctorId);
    List<Object> getPatients(UUID doctorId);
    List<Object> getActiveAdmissions(UUID doctorId);
    Optional<DoctorDto> getDoctorById(UUID doctorId);
    List<DoctorDto> findAll();
    List<DoctorDto> findByDepartment(UUID departmentId);
}
