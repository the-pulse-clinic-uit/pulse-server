package com.pulseclinic.pulse_server.modules.scheduling.entity;

import com.pulseclinic.pulse_server.enums.ShiftAssignmentRole;
import com.pulseclinic.pulse_server.enums.ShiftAssignmentStatus;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shift_assignments")
public class ShiftAssignment {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "duty_date")
    private LocalDate duty_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftAssignmentRole role_in_shift = ShiftAssignmentRole.PRIMARY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftAssignmentStatus status = ShiftAssignmentStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updated_at;

    // relationships 3

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "room_id")
    private Room room; // can override

}
