package com.pulseclinic.pulse_server.modules.appointments.dto;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.enums.AppointmentType;
import com.pulseclinic.pulse_server.modules.encounters.dto.followUpPlan.FollowUpPlanDto;
import com.pulseclinic.pulse_server.modules.encounters.entity.FollowUpPlan;
import com.pulseclinic.pulse_server.modules.patients.dto.PatientDto;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment.ShiftAssignmentDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.ShiftAssignment;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDto {
    private UUID id;

    private LocalDateTime starts_at;

    private LocalDateTime ends_at;

    private AppointmentStatus status;

    private AppointmentType type;

    private String description;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    // relationships => 5

    private PatientDto patient_dto;

    private DoctorDto doctor_dto;

    private ShiftAssignmentDto shift_assignment_dto;

    private FollowUpPlanDto follow_up_plan_dto;
}
