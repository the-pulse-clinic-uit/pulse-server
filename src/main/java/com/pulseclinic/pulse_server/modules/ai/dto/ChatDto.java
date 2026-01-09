package com.pulseclinic.pulse_server.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ChatDto {
    @NotBlank(message = "Message is required")
    private String message;

    private List<ChatHistoryItem> history;

    @Data
    public static class ChatHistoryItem {
        private String role; // "user" or "assistant"
        private String content;
    }
}
