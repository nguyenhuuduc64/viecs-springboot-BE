package com.service;

import com.dto.request.AIRequest;
import com.dto.response.AIResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIService {
    private ChatClient chatClient;
    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AIResponse analyzeTech(AIRequest aiRequest) throws ParseException {
        return AIResponse.builder()
                .responseMessage(chatClient.prompt(aiRequest.getMessage()).call().content())
                .build();
    }

    public AIResponse generateContent(String promp){
        return AIResponse.builder()
                .responseMessage(chatClient.prompt(promp).call().content())
                .build();
    }
}
