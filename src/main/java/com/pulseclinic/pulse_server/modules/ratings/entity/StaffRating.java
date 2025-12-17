package com.pulseclinic.pulse_server.modules.ratings.entity;

import com.pulseclinic.pulse_server.enums.RatingType;
import com.pulseclinic.pulse_server.modules.encounters.entity.Encounter;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
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
@Table(name= "staff_ratings")
public class StaffRating {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 256)
    private String comment;

    @Column(nullable = false, name = "guest_contact_type")
    private String guestContactType;

    @Column(name = "guest_contact_hash")
    private String guestContactHash;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingType raterType = RatingType.GUEST;

    @Column(nullable = false)
    private Integer rating; // 0<x<5

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    // relationships => 3

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "patient_id")
    private Patient patient; // optional

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter; // optional
}
