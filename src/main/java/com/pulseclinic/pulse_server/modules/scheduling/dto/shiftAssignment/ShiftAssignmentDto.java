package com.pulseclinic.pulse_server.modules.scheduling.dto.shiftAssignment;

import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.scheduling.dto.shift.ShiftDto;
import com.pulseclinic.pulse_server.modules.scheduling.entity.Shift;
import com.pulseclinic.pulse_server.modules.staff.dto.doctor.DoctorDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShiftAssignmentDto {
    private UUID id;

    private LocalDate duty_date;

    private ShiftAssignmentRole role_in_shift;

    private ShiftAssignmentStatus status;

    private String notes;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    // relationships 3

    private DoctorDto doctor_dto;

    private ShiftDto shift_dto;

    private RoomDto room_dto; // can override
}
