package com.pulseclinic.pulse_server.modules.users.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator // Hibernate 6
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Email
    @NotBlank
    @Size(max = 255)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashed_password;

    @NotBlank
    @Size(max = 255)
    @Column(name = "full_name", nullable = false, length = 255)
    private String full_name;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;

    @NotBlank
    @Size(max = 32)
    @Column(name = "citizen_id", nullable = false, length = 32, unique = true)
    private String citizen_id;

    @Size(max = 12)
    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "gender")
    @ColumnDefault("TRUE")
    private Boolean gender = true;

    @Column(name = "birth_date")
    private LocalDate birth_date;

    @Size(max = 512)
    @Column(name = "avatar_url", length = 512)
    private String avatar_url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("TRUE")
    private Boolean is_active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    // relationships
    @JoinColumn(name= "role_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
