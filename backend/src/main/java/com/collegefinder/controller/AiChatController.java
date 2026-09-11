package com.collegefinder.controller;

import com.collegefinder.dto.request.ChatRequest;
import com.collegefinder.dto.response.ChatResponse;
import com.collegefinder.service.AiChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Chatbot")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return aiChatService.chat(request.getMessage());
    }
}
