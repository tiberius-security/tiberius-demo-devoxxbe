package com.example.devoxxbe.controller;

import com.example.devoxxbe.service.ChatbotService;
import com.example.devoxxbe.service.ChatbotService.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody Map<String, String> request) {
        final String question = request.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.error("Question is required"));
        }

        final ChatResponse response = chatbotService.chat(question);

        if ("error".equals(response.status())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "VeloElite Customer Service Chatbot",
                "message", "Ready to assist with your cycling questions"
        ));
    }
}
