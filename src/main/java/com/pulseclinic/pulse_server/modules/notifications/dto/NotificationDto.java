package com.pulseclinic.pulse_server.modules.notifications.dto;

import com.pulseclinic.pulse_server.enums.NotificationChannel;
import com.pulseclinic.pulse_server.enums.NotificationStatus;
import com.pulseclinic.pulse_server.enums.NotificationType;
import com.pulseclinic.pulse_server.modules.users.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class NotificationDto {
    private UUID id;

    private NotificationType type;

    private NotificationChannel channel;

    private String title;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private NotificationStatus status;

    // relationships
    private UserDto userDto;
}
