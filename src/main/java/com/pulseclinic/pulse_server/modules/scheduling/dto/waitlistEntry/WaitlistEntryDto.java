package com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry;

import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.enums.WaitlistStatus;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.entity.Appointment;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitlistEntryDto {
    private UUID id;

    @NotNull(message = "Duty date is required")
    private LocalDate dutyDate; // to get all doctors exactly the moment the patient was added to the list

    private Integer ticketNo;

    private String notes;

    private WaitlistPriority priority;

    private WaitlistStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime calledAt;

    private LocalDateTime servedAt;

    // relationships => 3
    private AppointmentDto appointmentDto; // nullable

    private PatientDto patientDto;

    private DoctorDto doctorDto;
}
