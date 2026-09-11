package com.collegefinder.service.impl;

import com.collegefinder.dto.response.ChatResponse;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.entity.College;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.service.AiChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Grounded chatbot: retrieves matching rows from the college database first,
 * then (optionally) asks a configurable AI API to phrase the answer using
 * ONLY that retrieved context. If no AI_API_KEY is configured, it still
 * returns a useful answer built directly from the database results - it
 * never invents college facts either way.
 */
@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    private final CollegeRepository collegeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${app.ai.api-key:}")
    private String aiApiKey;

    @Value("${app.ai.api-url:https://api.anthropic.com/v1/messages}")
    private String aiApiUrl;

    @Value("${app.ai.model:claude-sonnet-4-5}")
    private String aiModel;

    public AiChatServiceImpl(CollegeRepository collegeRepository) {
        this.collegeRepository = collegeRepository;
    }

    private static final Pattern BUDGET_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(lakh|lakhs|l\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public ChatResponse chat(String message) {
        List<College> allColleges = collegeRepository.findAll();
        String lower = message.toLowerCase(Locale.ROOT);

        List<College> matches = allColleges.stream()
                .filter(c -> matchesQuery(c, lower))
                .limit(8)
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            // fall back to a small relevant sample so the assistant still has grounded context
            matches = allColleges.stream().limit(5).collect(Collectors.toList());
        }

        String context = buildContext(matches);

        String reply;
        if (aiApiKey == null || aiApiKey.isBlank()) {
            reply = buildFallbackReply(message, matches);
        } else {
            try {
                reply = callAiApi(message, context);
            } catch (Exception e) {
                log.warn("AI API call failed, falling back to database-only reply: {}", e.getMessage());
                reply = buildFallbackReply(message, matches);
            }
        }

        return ChatResponse.builder()
                .reply(reply)
                .referencedColleges(matches.stream().map(CollegeResponse::fromEntity).collect(Collectors.toList()))
                .build();
    }

    private boolean matchesQuery(College c, String lower) {
        boolean any = false;
        if (c.getCity() != null && lower.contains(c.getCity().toLowerCase())) any = true;
        if (c.getState() != null && lower.contains(c.getState().toLowerCase())) any = true;
        if (c.getCourses() != null) {
            for (String course : c.getCourses().split(";")) {
                if (lower.contains(course.trim().toLowerCase())) any = true;
            }
        }
        if (lower.contains("hostel") && Boolean.TRUE.equals(c.getHostel())) any = true;

        Matcher m = BUDGET_PATTERN.matcher(lower);
        if (m.find() && c.getAnnualFeeLakh() != null) {
            double budget = Double.parseDouble(m.group(1));
            if (c.getAnnualFeeLakh().compareTo(BigDecimal.valueOf(budget)) <= 0) any = true;
        }
        return any;
    }

    private String buildContext(List<College> matches) {
        StringBuilder sb = new StringBuilder();
        for (College c : matches) {
            sb.append("- ").append(c.getCollegeName())
                    .append(" | ").append(c.getCity()).append(", ").append(c.getState())
                    .append(" | type: ").append(c.getInstitutionType())
                    .append(" | courses: ").append(c.getCourses())
                    .append(" | annual fee: ~₹").append(c.getAnnualFeeLakh()).append("L")
                    .append(" | cutoff exam: ").append(c.getCutoffExam())
                    .append(" | placement rate: ").append(c.getPlacementRatePct()).append("%")
                    .append(" | avg package: ₹").append(c.getAveragePackageLpa()).append(" LPA")
                    .append(" | rating: ").append(c.getRating()).append("/5")
                    .append(" | hostel: ").append(Boolean.TRUE.equals(c.getHostel()) ? "Yes" : "No")
                    .append(" | NIRF rank: ").append(c.getNirfRank())
                    .append("\n");
        }
        return sb.toString();
    }

    private String buildFallbackReply(String message, List<College> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("Based on the college database, here's what matches your question:\n\n");
        for (College c : matches) {
            sb.append("• ").append(c.getCollegeName()).append(" (").append(c.getCity()).append(", ").append(c.getState()).append(") - ")
                    .append("fees ~₹").append(c.getAnnualFeeLakh()).append("L/yr, placement ")
                    .append(c.getPlacementRatePct()).append("%, rating ").append(c.getRating()).append("/5\n");
        }
        sb.append("\nThat information is not available in the current college dataset if it isn't listed above. ")
          .append("(Connect an AI_API_KEY on the backend for more natural, conversational answers.)");
        return sb.toString();
    }

    private String callAiApi(String userMessage, String context) throws Exception {
        String systemPrompt = "You are a college recommendation assistant. Answer ONLY using the college data " +
                "provided below. Never invent college names, fees, rankings, placement numbers, cutoffs, or ratings. " +
                "If the answer isn't in the data, say: 'That information is not available in the current college dataset.'\n\n" +
                "COLLEGE DATA:\n" + context;

        String body = objectMapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
            put("model", aiModel);
            put("max_tokens", 1000);
            put("system", systemPrompt);
            put("messages", List.of(new java.util.LinkedHashMap<String, Object>() {{
                put("role", "user");
                put("content", userMessage);
            }}));
        }});

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiApiUrl))
                .header("Content-Type", "application/json")
                .header("x-api-key", aiApiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("AI API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentArray = root.path("content");
        StringBuilder text = new StringBuilder();
        if (contentArray.isArray()) {
            for (JsonNode block : contentArray) {
                if ("text".equals(block.path("type").asText())) {
                    text.append(block.path("text").asText());
                }
            }
        }
        return text.length() > 0 ? text.toString() : buildFallbackReply(userMessage, List.of());
    }
}
