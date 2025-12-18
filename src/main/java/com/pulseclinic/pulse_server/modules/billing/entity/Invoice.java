package com.pulseclinic.pulse_server.modules.billing.entity;

import com.pulseclinic.pulse_server.enums.InvoiceStatus;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(nullable = false, name = "due_date")
    private LocalDate dueDate;

    @ColumnDefault("0.00")
    @Column(nullable = false, name = "amount_paid")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @ColumnDefault("0.00")
    @Column(nullable = false, name = "total_amount")
    private BigDecimal totalAmount;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // relationships => 1

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

}