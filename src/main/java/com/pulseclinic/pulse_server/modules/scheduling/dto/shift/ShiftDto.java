package com.pulseclinic.pulse_server.modules.scheduling.dto.shift;

import com.pulseclinic.pulse_server.enums.ShiftKind;
import com.pulseclinic.pulse_server.modules.rooms.dto.RoomDto;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.staff.dto.department.DepartmentDto;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShiftDto {
    private UUID id;

    private String name;

    private ShiftKind kind;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer slotMinutes;

    private Integer capacityPerSlot; // default 1

    private LocalDateTime createdAt;

    // relationship
    private DepartmentDto departmentDto;

    private RoomDto defaultRoomDto;
}
