package com.pulseclinic.pulse_server.security.service;

import com.pulseclinic.pulse_server.security.config.OtpProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    private static final SecureRandom secureRandom = new SecureRandom();

    private final OtpProperties props;
    private final StringRedisTemplate redis;

    public OtpService(OtpProperties props, StringRedisTemplate redis) {
        this.props = props;
        this.redis = redis;
    }

    public String requestOtp(String emailRaw){
        String email = normalizeEmail(emailRaw);

        String rlKey = rlKey(email); // set key redis
        boolean allowed = Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(rlKey, "1", Duration.ofSeconds(props.requestCooldownSeconds())));
        if (!allowed) {
            throw new TooManyRequestsException("Please wait for " + props.requestCooldownSeconds() + " seconds before attempting request");
        }

        String otp = generate6Digits();
        String hashedOtp = hash(email, otp, props.pepper());
        redis.opsForValue().set(otpKey(email), hashedOtp, Duration.ofMinutes(props.ttlMinutes()));

        // reset attempts
        redis.delete(attemptsKey(email));
        return otp;
    }

    public void verifyOtp(String emailRaw, String otpRaw) {
        String email = normalizeEmail(emailRaw);
        String otp = otpRaw == null ? "" : otpRaw.trim();

        String storedHash = redis.opsForValue().get(otpKey(email));
        if (storedHash == null) {
            throw new OtpInvalidException("OTP expired or not found");
        }
        // check attempts
        long attempts = getAttempts(email);
        if (attempts >= props.maxAttempts()){
            redis.delete(otpKey(email));
            throw new OtpLockedException("Too many failed attempts. Please try again.");
        }

        String inputHash = hash(email, otp, props.pepper());
        if (!storedHash.equals(inputHash)) {
            long newAttempts = incrementAttempts(email);
            if (newAttempts >= props.maxAttempts()){
                redis.delete(otpKey(email));
                throw new OtpLockedException("Too many failed attempts. Please try again.");
            }
            throw new OtpInvalidException("Invalid OTP");
        }

        // delete otp + attempts
        redis.delete(otpKey(email));
        redis.delete(attemptsKey(email));
    }

    String generate6Digits() {
        int v = secureRandom.nextInt(1_000_000);
        return String.format(Locale.ROOT,"%06d", v);
    }

    // helpers
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String otpKey(String email) {return "pwdreset:otp:" + normalizeEmail(email);}
    private String rlKey(String email) {return "pwdreset:rl:" + normalizeEmail(email);}
    private String attemptsKey(String email) {return "pwdreset:attempts:" + normalizeEmail(email);}

    private long getAttempts(String email) {
        String v = redis.opsForValue().get(attemptsKey(email));
        try { return v == null ? 0 : Long.parseLong(v); }
        catch (NumberFormatException e) { return 0; }
    }

    private long incrementAttempts(String email) {
        String key = attemptsKey(email);
        Long v = redis.opsForValue().increment(key);
        long attempts = v == null ? 0 : v;

        if (attempts == 1){
            redis.expire(key, Duration.ofMinutes(props.ttlMinutes()));
        }
        return attempts;
    }

    // save to redis but encrypted
    private String hash(String email, String otp, String pepper) {
        String input = otp + ":" + pepper + ":" + email;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // exceptions
    public static class TooManyRequestsException extends RuntimeException {
        public TooManyRequestsException(String m) { super(m); }
    }
    public static class OtpInvalidException extends RuntimeException {
        public OtpInvalidException(String m) { super(m); }
    }
    public static class OtpLockedException extends RuntimeException {
        public OtpLockedException(String m) { super(m); }
    }
}
