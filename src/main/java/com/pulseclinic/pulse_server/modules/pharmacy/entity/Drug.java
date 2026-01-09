package com.pulseclinic.pulse_server.modules.pharmacy.entity;

import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "dosage_form")
    private DrugDosageForm dosageForm = DrugDosageForm.CAPSULE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrugUnit unit = DrugUnit.CAPSULE;

    @Column(nullable = false)
    private String strength;

    @Builder.Default
    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @ColumnDefault("0.00")
    @Column(nullable = false, name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Builder.Default
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Builder.Default
    @ColumnDefault("10")
    @Column(nullable = false, name = "min_stock_level")
    private Integer minStockLevel = 10;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
