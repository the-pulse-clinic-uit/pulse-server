package com.pulseclinic.pulse_server.modules.pharmacy.entity;

import com.pulseclinic.pulse_server.enums.PrescriptionStatus;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ColumnDefault("0.00")
    @Column(nullable = false, name = "total_price")
    private BigDecimal totalPrice;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Column(name  = "created_at", nullable = false)
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.DRAFT;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // relationship

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;


}
