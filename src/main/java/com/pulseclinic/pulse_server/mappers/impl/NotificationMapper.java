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

    public NotificationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public NotificationDto mapTo(Notification notification) {
        return this.modelMapper.map(notification, NotificationDto.class);
    }

    @Override
    public Notification mapFrom(NotificationDto notificationDto) {
        return this.modelMapper.map(notificationDto, Notification.class);
    }

    public Notification mapFrom(NotificationRequestDto notificationRequestDto) {
        return this.modelMapper.map(notificationRequestDto, Notification.class);
    }
}
