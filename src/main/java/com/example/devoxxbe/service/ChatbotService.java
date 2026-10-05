package com.example.devoxxbe.service;

import com.example.devoxxbe.guardrail.ContentSafetyInputGuardrail;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatbotService {

    private final ChatModel chatModel;
    private final ContentSafetyInputGuardrail contentSafetyGuardrail;
    private final List<InputGuardrail> inputGuardrails;

    @Value("${guardrails.content-safety.enabled:true}")
    private boolean contentSafetyEnabled;

    private ChatbotAssistant assistant;

    public ChatbotService(
            final ChatModel chatModel,
            final ContentSafetyInputGuardrail contentSafetyGuardrail
    ) {
        this.chatModel = chatModel;
        this.contentSafetyGuardrail = contentSafetyGuardrail;
        this.inputGuardrails = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        // Build guardrail list based on configuration
        if (contentSafetyEnabled) {
            inputGuardrails.add(contentSafetyGuardrail);
            log.info("ContentSafetyInputGuardrail ENABLED");
        } else {
            log.warn("ContentSafetyInputGuardrail DISABLED - bot is vulnerable to attacks!");
        }

        // Build assistant with configured guardrails
        this.assistant = AiServices.builder(ChatbotAssistant.class)
                .chatModel(chatModel)
                .inputGuardrails(inputGuardrails.toArray(new InputGuardrail[0]))
                .build();

        log.info("ChatbotService initialized with {} input guardrails", inputGuardrails.size());
    }

    public ChatResponse chat(final String userMessage) {
        log.debug("Received message: {}", userMessage);

        try {
            String response = assistant.chat(userMessage);
            log.debug("Generated response: {}", response);
            return ChatResponse.success(response);
        } catch (final InputGuardrailException exc) {
            log.warn("Input guardrail blocked message: {}", exc.getMessage());
            return ChatResponse.error(exc.getMessage());
        } catch (final Exception exc) {
            log.error("Error generating response", exc);
            return ChatResponse.error("Unable to process your request. Please try again later.");
        }
    }

    public record ChatResponse(String response, String status, String error) {
        public static ChatResponse success(String response) {
            return new ChatResponse(response, "success", null);
        }

        public static ChatResponse error(String error) {
            return new ChatResponse(null, "error", error);
        }
    }
}
