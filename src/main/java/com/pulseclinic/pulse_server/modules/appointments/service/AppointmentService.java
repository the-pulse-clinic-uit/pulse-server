package com.pulseclinic.pulse_server.modules.appointments.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;
import com.pulseclinic.pulse_server.modules.encounters.dto.encounter.EncounterDto;

public interface AppointmentService {
    AppointmentDto scheduleAppointment(AppointmentRequestDto appointmentRequestDto);
    Optional<AppointmentDto> getAppointmentById(UUID appointmentId);
    boolean rescheduleAppointment(UUID appointmentId, LocalDateTime newStartTime, LocalDateTime newEndTime);
    boolean cancelAppointment(UUID appointmentId, String reason);
    boolean confirmAppointment(UUID appointmentId);
    boolean checkIn(UUID appointmentId);
    boolean markAsDone(UUID appointmentId);
    EncounterDto createEncounter(UUID appointmentId);
}
