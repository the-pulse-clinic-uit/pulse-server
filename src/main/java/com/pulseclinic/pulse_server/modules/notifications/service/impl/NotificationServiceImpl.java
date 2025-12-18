package com.pulseclinic.pulse_server.modules.notifications.service.impl;

import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationDto;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.modules.notifications.entity.Notification;
import com.pulseclinic.pulse_server.modules.notifications.repository.NotificationRepository;
import com.pulseclinic.pulse_server.modules.notifications.service.NotificationService;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification create(NotificationRequestDto notificationRequestDto) {
        User user = this.userRepository.findById(notificationRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(notificationRequestDto.getType())
                .channel(notificationRequestDto.getChannel())
                .title(notificationRequestDto.getTitle())
                .content(notificationRequestDto.getContent())
                .status(notificationRequestDto.getStatus())
                .isRead(false)
                .build();

        return this.notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return this.notificationRepository.findById(id);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return this.notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return this.notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    @Override
    public Integer countUnreadByUserId(UUID userId) {
        return this.notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public Notification markAsRead(UUID id) {
        Notification notification = this.notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        return this.notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = this.notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
        notifications.forEach(notification -> notification.setIsRead(true));
        this.notificationRepository.saveAll(notifications);
    }
}
