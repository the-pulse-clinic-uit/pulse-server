package com.pulseclinic.pulse_server.modules.notifications.service.impl;

import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationDto;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.modules.notifications.entity.Notification;
import com.pulseclinic.pulse_server.modules.notifications.repository.NotificationRepository;
import com.pulseclinic.pulse_server.modules.notifications.service.NotificationService;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import com.pulseclinic.pulse_server.security.service.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    UserRepository userRepository,
                                    EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
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

        Notification savedNotification = this.notificationRepository.save(notification);

        // Send email notification
        try {
            this.emailService.sendNotification(
                    user.getEmail(),
                    notificationRequestDto.getTitle(),
                    notificationRequestDto.getContent()
            );
            savedNotification.setSentAt(LocalDateTime.now());
            savedNotification.setStatus(com.pulseclinic.pulse_server.enums.NotificationStatus.SENT);
        } catch (Exception e) {
            savedNotification.setStatus(com.pulseclinic.pulse_server.enums.NotificationStatus.FAILED);
        }

        return this.notificationRepository.save(savedNotification);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return this.notificationRepository.findById(id);
    }

    @Override
    public List<Notification> findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return this.notificationRepository.findByUserIdOrderByCreatedAtDesc(user.get().getId());
        }
        else throw new RuntimeException("User not found");
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
    public List<Notification> findUnreadByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return this.notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(user.get().getId(), false);
        }
        else throw new RuntimeException("User not found");
    }

    @Override
    public Integer countUnreadByUserId(UUID userId) {
        return this.notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public Integer countUnreadByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return this.notificationRepository.countUnreadByUserId(user.get().getId());
        }
        else throw new RuntimeException("User not found");
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

    @Override
    public void markAllAsReadByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            List<Notification> notifications = this.notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(user.get().getId(), false);
            notifications.forEach(notification -> notification.setIsRead(true));
            this.notificationRepository.saveAll(notifications);
        }
    }
}
