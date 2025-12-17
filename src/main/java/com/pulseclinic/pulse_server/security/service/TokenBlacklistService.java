package com.pulseclinic.pulse_server.security.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Service
public class TokenBlacklistService {
    private final StringRedisTemplate redis;

    public TokenBlacklistService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void blacklistToken(String token, long ttlSeconds) {
        String key = "blacklist:token:" + sha256(token);
        redis.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String token) {
        String key = "blacklist:token:" + sha256(token);
        return Boolean.TRUE.equals(redis.hasKey(key));
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
