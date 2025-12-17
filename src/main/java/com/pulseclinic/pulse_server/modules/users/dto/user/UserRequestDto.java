package com.pulseclinic.pulse_server.modules.users.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRequestDto {
    //@NotBlank(message = "Email is required")
//    @Email(message = "Email must be valid")
//    @Size(max = 255, message = "Email must not exceed 255 characters")
//    private String email;

    //@NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String hashed_password;

    //@NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String full_name;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Citizen ID must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Citizen ID must contain only numbers")
    private String citizen_id;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number must be valid")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private Boolean gender;

    @Past(message = "Birth date must be in the past")
    private LocalDate birth_date;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatar_url;

    // relationships
    @NotNull(message = "Role is required")
    private UUID role_id;
}
