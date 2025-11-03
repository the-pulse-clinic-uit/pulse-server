package com.pulseclinic.pulse_server.modules.appointments.dto;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.AppointmentType;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentRequestDto {
    private LocalDateTime starts_at;

    private LocalDateTime ends_at;

    @NotNull(message = "Appointment Status is required. Valid values: 'PENDING', 'CONFIRMED', 'CANCELLED', 'NO_SHOW', 'DONE'")
    private AppointmentStatus status;

    @NotNull(message = "Appointment Type is required. Valid values: 'NORMAL', 'FOLLOW_UP', 'EMERGENCY'")
    private AppointmentType type;

    private String description;

    // relationships => 5

    @NotNull(message = "Patient ID is required")
    private UUID patient_id;

    @NotNull(message = "Doctor ID is required")
    private UUID doctor_id;

    private UUID shift_assignment_id;

    private UUID follow_up_plan_id;
}
