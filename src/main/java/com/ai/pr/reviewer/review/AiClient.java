package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ReviewConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI 客户端封装
 */
public class AiClient {
    private static final Logger logger = LoggerFactory.getLogger(AiClient.class);

    private final ChatLanguageModel model;
    private final ObjectMapper objectMapper;

    public AiClient(ReviewConfig config) {
        String provider = config.getAi().getProvider();
        String apiKey = config.getAi().getApiKey();

        if (apiKey == null || apiKey.isBlank()) {
            if ("deepseek".equals(provider)) {
                throw new IllegalStateException("DEEPSEEK_API_KEY not set");
            }
            throw new IllegalStateException("ANTHROPIC_API_KEY not set");
        }

        if ("deepseek".equals(provider)) {
            String baseUrl = config.getAi().getBaseUrl();
            this.model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl != null ? baseUrl : "https://api.deepseek.com")
                .modelName(config.getAi().getModel())
                .temperature(config.getAi().getTemperature())
                .maxTokens(config.getAi().getMaxTokens())
                .build();
        } else {
            this.model = AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(config.getAi().getModel())
                .temperature(config.getAi().getTemperature())
                .maxTokens(config.getAi().getMaxTokens())
                .build();
        }

        this.objectMapper = new ObjectMapper();
    }

    public String generate(String prompt) {
        logger.debug("Sending prompt to AI (length: {})", prompt.length());
        String response = model.generate(prompt);
        logger.debug("Received response (length: {})", response != null ? response.length() : 0);
        return response;
    }

    public JsonNode generateJson(String prompt) {
        String response = generate(prompt);
        logger.info("AI response length: {}", response != null ? response.length() : 0);
        try {
            String json = extractJson(response);
            logger.info("Extracted JSON length: {}", json.length());
            return objectMapper.readTree(json);
        } catch (Exception e) {
            logger.warn("Failed to parse AI response as JSON, trying to repair...", e);
            // Try to repair truncated JSON
            try {
                String json = extractJson(response);
                String repaired = repairTruncatedJson(json);
                if (!repaired.equals(json)) {
                    logger.info("Trying repaired JSON");
                    return objectMapper.readTree(repaired);
                }
            } catch (Exception e2) {
                logger.warn("Failed to repair JSON", e2);
            }
            return null;
        }
    }

    private String extractJson(String response) {
        if (response == null) {
            return "{}";
        }
        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1) {
            return response.substring(firstBrace, lastBrace + 1);
        }
        return response;
    }

    private String repairTruncatedJson(String json) {
        // Try to fix truncated findings array
        if (json.contains("\"findings\":")) {
            // Count opening and closing brackets/braces
            int openBrackets = 0, closeBrackets = 0;
            int openBraces = 0, closeBraces = 0;
            for (char c : json.toCharArray()) {
                if (c == '[') openBrackets++;
                if (c == ']') closeBrackets++;
                if (c == '{') openBraces++;
                if (c == '}') closeBraces++;
            }
            // Try to close findings array and object
            StringBuilder sb = new StringBuilder(json);
            while (closeBrackets < openBrackets) {
                sb.append("]");
                closeBrackets++;
            }
            while (closeBraces < openBraces) {
                sb.append("}");
                closeBraces++;
            }
            return sb.toString();
        }
        return json;
    }
}
