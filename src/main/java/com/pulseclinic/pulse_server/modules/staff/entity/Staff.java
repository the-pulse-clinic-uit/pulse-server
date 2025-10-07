package com.pulseclinic.pulse_server.modules.staff.entity;

import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position = Position.STAFF;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    // relationships

    @JoinColumn(name="user_id")
    @OneToOne(fetch = FetchType.EAGER)
    private User user;

}
