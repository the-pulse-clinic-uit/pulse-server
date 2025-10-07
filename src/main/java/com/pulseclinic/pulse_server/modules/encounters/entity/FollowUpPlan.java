package com.pulseclinic.pulse_server.modules.encounters.entity;

import com.pulseclinic.pulse_server.enums.FollowUpPlanStatus;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "follow_up_plans")
@Entity
public class FollowUpPlan {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "first_due_at")
    private LocalDateTime first_due_at;

    @Column(name = "rrule", nullable = false, columnDefinition = "TEXT")
    private String rrule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FollowUpPlanStatus status = FollowUpPlanStatus.ACTIVE;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String notes;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();
    // relationships

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "base_encounter_id")
    private Encounter base_encounter;
}
