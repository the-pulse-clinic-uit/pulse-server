package com.pulseclinic.pulse_server.modules.admissions.entity;

import com.pulseclinic.pulse_server.enums.AdmissionStatus;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "admissions")
public class Admission {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdmissionStatus status = AdmissionStatus.ONGOING;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String notes;

    @Column(nullable = false, name = "admitted_at")
    @Builder.Default
    private LocalDateTime admittedAt = LocalDateTime.now();

    @Column(name = "discharged_at")
    private LocalDateTime dischargedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // relationships => 4
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter; // optional

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

}
