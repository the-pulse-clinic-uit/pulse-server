package com.pulseclinic.pulse_server.modules.staff.entity;

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
@Table(name = "doctors")
public class Doctor {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique=true, nullable=false, length=50, name = "license_id")
    private String licenseId;

    @Column(name = "is_verified")
    @ColumnDefault("FALSE")
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    @ColumnDefault("0")
    @Builder.Default
    private Integer ratingCount = 0;

    // relationships => 1

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="staff_id")
    private Staff staff;

    // helper method to get department through staff
    public Department getDepartment() {
        return staff != null ? staff.getDepartment() : null;
    }
}
