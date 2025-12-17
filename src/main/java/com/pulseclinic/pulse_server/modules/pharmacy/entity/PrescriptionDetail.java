package com.pulseclinic.pulse_server.modules.pharmacy.entity;

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
@Table(name = "prescription_details")
public class PrescriptionDetail {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "TEXT", name = "strength_text")
    private String strength_text;

    @ColumnDefault("0")
    @Column(nullable = false, name = "quantity")
    @Builder.Default
    private Integer quantity = 0;

    @ColumnDefault("0.00")
    @Column(name = "unit_price", nullable = false)
    @Builder.Default
    private BigDecimal unit_price = BigDecimal.ZERO;

    @ColumnDefault("0.00")
    @Column(name = "item_total_price", nullable = false)
    @Builder.Default
    private BigDecimal item_total_price = BigDecimal.ZERO;

    @Column(name = "dose", nullable = false)
    private String dose; // eg 1 tablet

    @Column(name = "timing", nullable = false)
    private String timing; // before meal, after meal

    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(nullable = false, name = "frequency")
    private String frequency; // 2 times per day

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    // relationships => 2

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;
}
