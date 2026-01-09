package com.pulseclinic.pulse_server.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {
    private String reply;
    private List<ChatDto.ChatHistoryItem> history;
}
