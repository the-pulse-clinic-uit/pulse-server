package com.pulseclinic.pulse_server.modules.notifications.service;

import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationDto;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.modules.notifications.entity.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {
    Notification create(NotificationRequestDto notificationRequestDto);
    Optional<Notification> findById(UUID id);
    List<Notification> findByUserId(UUID userId);
    List<Notification> findUnreadByUserId(UUID userId);
    Integer countUnreadByUserId(UUID userId);
    Notification markAsRead(UUID id);
    void markAllAsRead(UUID userId);
}
