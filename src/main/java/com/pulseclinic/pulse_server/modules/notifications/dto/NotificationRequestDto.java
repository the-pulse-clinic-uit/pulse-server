package com.pulseclinic.pulse_server.modules.notifications.dto;

import com.pulseclinic.pulse_server.enums.NotificationChannel;
import com.pulseclinic.pulse_server.enums.NotificationStatus;
import com.pulseclinic.pulse_server.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class NotificationRequestDto {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private NotificationType type;

    private NotificationChannel channel;

    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    private String content;

    private NotificationStatus status;
}
