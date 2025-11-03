package com.pulseclinic.pulse_server.modules.patients.entity;

import com.pulseclinic.pulse_server.enums.BloodType;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "health_insurance_id", nullable = false, length = 64, unique = true)
    private String health_insurance_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodType bloodType = BloodType.O;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String allergies;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    // relationships

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
