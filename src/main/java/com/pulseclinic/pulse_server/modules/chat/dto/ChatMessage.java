package com.pulseclinic.pulse_server.modules.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String senderRole; // PATIENT, GUEST, STAFF, DOCTOR
    private String recipientId;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT,
        STAFF_AVAILABLE,
        STAFF_UNAVAILABLE,
        TYPING,
        PATIENT_CONNECTED,
        PATIENT_DISCONNECTED
    }
}
