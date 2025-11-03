package com.pulseclinic.pulse_server.modules.scheduling.entity;

import com.pulseclinic.pulse_server.enums.ShiftKind;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftKind kind = ShiftKind.CLINIC;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime start_time;

    @Column(nullable = false, name = "end_time")
    private LocalDateTime end_time;

    @Column(nullable = false, name = "slot_minutes")
    @ColumnDefault("30")
    private Integer slot_minutes;

    @Column(nullable = false, name = "capacity_per_slot")
    @ColumnDefault("1")
    private Integer capacity_per_slot; // default 1

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    // relationship

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "default_room_id")
    private Room default_room;

}


