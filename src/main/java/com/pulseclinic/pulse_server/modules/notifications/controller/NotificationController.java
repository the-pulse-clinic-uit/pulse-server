package com.pulseclinic.pulse_server.modules.notifications.controller;

import com.pulseclinic.pulse_server.mappers.impl.NotificationMapper;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationDto;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.modules.notifications.entity.Notification;
import com.pulseclinic.pulse_server.modules.notifications.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService,
            NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<NotificationDto> createNotification(
            @RequestBody NotificationRequestDto notificationRequestDto) {
        Notification notification = this.notificationService.create(notificationRequestDto);
        return new ResponseEntity<>(this.notificationMapper.mapTo(notification), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(Authentication authentication) {
        List<Notification> notifications = this.notificationService.findByEmail(authentication.getName());
        return ResponseEntity.ok(notifications.stream()
                .map(this.notificationMapper::mapTo)
                .collect(Collectors.toList()));
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationDto>> getMyUnreadNotifications(Authentication authentication) {
        List<Notification> notifications = this.notificationService.findUnreadByEmail(authentication.getName());
        return ResponseEntity.ok(notifications.stream()
                .map(this.notificationMapper::mapTo)
                .collect(Collectors.toList()));
    }

    @GetMapping("/me/unread/count")
    public ResponseEntity<Integer> getMyUnreadCount(Authentication authentication) {
        Integer count = this.notificationService.countUnreadByEmail(authentication.getName());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getById(@PathVariable UUID id) {
        Optional<Notification> notification = this.notificationService.findById(id);
        if (notification.isPresent()) {
            return ResponseEntity.ok(this.notificationMapper.mapTo(notification.get()));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable UUID id) {
        Notification notification = this.notificationService.markAsRead(id);
        return ResponseEntity.ok(this.notificationMapper.mapTo(notification));
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        this.notificationService.markAllAsReadByEmail(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('doctor', 'staff')")
    public ResponseEntity<List<NotificationDto>> getNotificationsByUserId(@PathVariable UUID userId) {
        List<Notification> notifications = this.notificationService.findByUserId(userId);
        return ResponseEntity.ok(notifications.stream()
                .map(this.notificationMapper::mapTo)
                .collect(Collectors.toList()));
    }
}
