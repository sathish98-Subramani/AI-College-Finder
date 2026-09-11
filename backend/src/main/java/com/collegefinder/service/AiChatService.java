package com.collegefinder.service;

import com.collegefinder.dto.response.ChatResponse;

public interface AiChatService {
    ChatResponse chat(String message);
}
