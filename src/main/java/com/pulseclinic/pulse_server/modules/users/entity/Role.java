package com.pulseclinic.pulse_server.modules.users.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;


    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();
}
