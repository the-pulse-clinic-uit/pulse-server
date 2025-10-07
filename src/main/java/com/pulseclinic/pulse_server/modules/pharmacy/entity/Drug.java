package com.pulseclinic.pulse_server.modules.pharmacy.entity;

import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "dosage_form")
    private DrugDosageForm dosage_form = DrugDosageForm.CAPSULE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrugUnit unit = DrugUnit.CAPSULE;

    @Column(nullable = false)
    private String strength;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    @ColumnDefault("0.00")
    @Column(nullable = false, name = "unit_price")
    private BigDecimal unit_price;
}
