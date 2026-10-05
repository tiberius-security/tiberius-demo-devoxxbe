package com.example.devoxxbe.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@NoArgsConstructor
public class ContentSafetyInputGuardrail implements InputGuardrail {

    private static final List<String> PROHIBITED_WORDS = List.of(
            "hack", "exploit", "bypass", "illegal", "fraud", "crack", "breach",
            "penetrate", "malware", "virus", "trojan", "backdoor", "phishing",
            "spam", "scam", "steal", "theft", "identity", "password", "credential"
    );

    private static final Set<String> BLOCKED_TOPICS = Set.of(
            "repository", "api", "token", "ldap",
            "cache", "database", "admin"
    );

    private static final List<Pattern> THREAT_PATTERNS = List.of(
            Pattern.compile("h[4@]ck", Pattern.CASE_INSENSITIVE),
            Pattern.compile("cr[4@]ck", Pattern.CASE_INSENSITIVE),
            Pattern.compile("expl[0o]it", Pattern.CASE_INSENSITIVE),
            Pattern.compile("byp[4@]ss", Pattern.CASE_INSENSITIVE),
            Pattern.compile("m[4@]lw[4@]re", Pattern.CASE_INSENSITIVE),
            Pattern.compile("[\\w\\s]*(?:how\\s+to|teach\\s+me|show\\s+me)\\s+(?:hack|exploit|bypass)", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public InputGuardrailResult validate(final UserMessage userMessage) {
        final String originalText = userMessage.singleText();
        final String text = originalText.toLowerCase();

        // Check for empty or blank input
        if (!StringUtils.hasText(text) || originalText.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty or blank");
        }

        // Check for too short input (less than 5 characters)
        if (originalText.trim().length() < 5) {
            return failure("Your message is too short. Please provide more details.");
        }

        // Check for too long input (more than 100 characters)
        if (originalText.length() > 500) {
            return failure("Your message is too long. Please keep it under 500 characters.");
        }

        // Check for excessive special characters (over 15% ratio)
        long specialCharCount = originalText.chars()
                .filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch))
                .count();
        double specialCharRatio = (double) specialCharCount / originalText.length();
        if (specialCharRatio > 0.15) {
            return failure("Your message contains suspicious character substitutions.");
        }

        // Check for prohibited words
        for (String word : PROHIBITED_WORDS) {
            if (text.contains(word)) {
                return failure("Your message contains prohibited content related to security threats.");
            }
        }

        // Check for threat patterns
        for (Pattern pattern : THREAT_PATTERNS) {
            if (pattern.matcher(originalText).find()) {
                return failure("Your message contains potentially harmful content patterns.");
            }
        }

        // Check for blocked topics
        return BLOCKED_TOPICS.stream()
                .filter(text::contains)
                .findFirst()
                .map(term -> failure("I can't discuss that topic. I'm here to help with cycling questions."))
                .orElse(success());
    }
}
