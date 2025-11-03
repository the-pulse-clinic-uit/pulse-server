package com.pulseclinic.pulse_server.modules.rooms.dto;

import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RoomDto {
    private UUID id;

    private String room_number; // etc B104

    private Integer bed_amount;

    private Boolean is_available;

    private LocalDateTime created_at;

    // relationships
    private DepartmentDto department_dto;
}
