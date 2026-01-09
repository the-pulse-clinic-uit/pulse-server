package com.pulseclinic.pulse_server.modules.ai.controller;

import com.pulseclinic.pulse_server.modules.ai.dto.ChatDto;
import com.pulseclinic.pulse_server.modules.ai.dto.ChatResponseDto;
import com.pulseclinic.pulse_server.modules.ai.service.ChatAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
@Tag(name = "AI Chat", description = "AI-powered chat assistant for clinic support")
public class ChatAiController {

    private final ChatAiService chatAiService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Chat with AI assistant",
        description = "Send a message to the AI assistant and receive a helpful response. " +
            "The assistant can answer questions about medications, doctors, departments, and appointments."
    )
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatDto chatDto) {
        ChatResponseDto response = chatAiService.chat(chatDto);
        return ResponseEntity.ok(response);
    }
}
