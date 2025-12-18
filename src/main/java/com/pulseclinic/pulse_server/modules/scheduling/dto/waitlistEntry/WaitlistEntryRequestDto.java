package com.pulseclinic.pulse_server.modules.scheduling.dto.waitlistEntry;

import com.pulseclinic.pulse_server.enums.WaitlistPriority;
import com.pulseclinic.pulse_server.enums.WaitlistStatus;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitlistEntryRequestDto {
    @NotNull(message = "Duty Date is required")
    private LocalDate dutyDate; // to get all doctors exactly the moment the patient was added to the list

    private Integer ticketNo;

    private String notes;

    @NotNull(message = "Priority is required. Valid values: NORMAL, URGENT, EMERGENCY")
    private WaitlistPriority priority;

    private WaitlistStatus status;

    private LocalDateTime calledAt;

    private LocalDateTime servedAt;

    // relationships => 3
    private UUID appointmentId; // nullable

    @NotNull(message = "Patient ID must not be empty")
    private UUID patientId;

    @NotNull(message = "Doctor ID must not be empty")
    private UUID doctorId;
}
