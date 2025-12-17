package com.pulseclinic.pulse_server.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        int ttlMinutes,
        int requestCooldownSeconds,
        int maxAttempts,
        String pepper
) {}
