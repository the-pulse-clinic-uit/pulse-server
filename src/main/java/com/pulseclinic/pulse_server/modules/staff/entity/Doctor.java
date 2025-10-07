package com.pulseclinic.pulse_server.modules.staff.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique=true, nullable=false, length=50)
    private String license_id;

    @Column(name = "is_verified")
    @ColumnDefault("FALSE")
    private Boolean is_verified = false;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    // relationships => 2

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
