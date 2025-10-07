package com.pulseclinic.pulse_server.modules.staff.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();
}
