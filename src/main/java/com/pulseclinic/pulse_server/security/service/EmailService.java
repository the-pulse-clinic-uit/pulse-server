package com.pulseclinic.pulse_server.security.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetOtp(String to, String otp) {
        String subject = "Password Reset OTP";
        String html = buildOtpHtml(otp);

        sendHtml(to, subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email",e);
        }
    }

    private String buildOtpHtml(String otp) {
        return """
            <div style="font-family:Arial,sans-serif;line-height:1.6">
              <h2>Password reset OTP</h2>
              <p>Your OTP:</p>
              <p style="font-size:24px;font-weight:bold;letter-spacing:2px">%s</p>
              <p>This code will expire in <b>10 minutes</b>.</p>
              <p>If you did not request this, you can ignore this email.</p>
            </div>
            """.formatted(otp);
    }
}
