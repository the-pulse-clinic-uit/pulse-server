package com.pulseclinic.pulse_server.mappers.impl;

import com.pulseclinic.pulse_server.mappers.Mapper;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationDto;
import com.pulseclinic.pulse_server.modules.notifications.dto.NotificationRequestDto;
import com.pulseclinic.pulse_server.modules.notifications.entity.Notification;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements Mapper<Notification, NotificationDto> {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public NotificationMapper(ModelMapper modelMapper, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

    @Override
    public NotificationDto mapTo(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .status(notification.getStatus())
                .userDto(notification.getUser() != null ? userMapper.mapTo(notification.getUser()) : null)
                .build();
    }

    @Override
    public Notification mapFrom(NotificationDto notificationDto) {
        return this.modelMapper.map(notificationDto, Notification.class);
    }

    public Notification mapFrom(NotificationRequestDto notificationRequestDto) {
        return this.modelMapper.map(notificationRequestDto, Notification.class);
    }
}
