package com.pulseclinic.pulse_server.modules.rooms.entity;

import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 4, unique = true)
    private String room_number; // etc B104

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer bed_amount;

    @Column(name = "is_available")
    @ColumnDefault("TRUE")
    private Boolean is_available;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    // relationships

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "department_id")
    private Department department;
}
