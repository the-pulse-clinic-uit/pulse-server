package com.pulseclinic.pulse_server.modules.admissions.service;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionDto;
import com.pulseclinic.pulse_server.modules.admissions.dto.AdmissionRequestDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface AdmissionService {
    AdmissionDto admitPatient(AdmissionRequestDto admissionRequestDto);
    boolean transferRoom(UUID admissionId, UUID newRoomId);
    boolean updateNotes(UUID admissionId, String notes);
    boolean updateStatus(UUID admissionId, AdmissionStatus status);
    boolean dischargePatient(UUID admissionId);
    Duration getDuration(UUID admissionId);
    Optional<PatientDto> getPatient(UUID admissionId);
    Optional<DoctorDto> getDoctor(UUID admissionId);
    Optional<RoomDto> getRoom(UUID admissionId);
    boolean isOngoing(UUID admissionId);
    boolean canTransfer(UUID admissionId);
    boolean canDischarge(UUID admissionId);
}
