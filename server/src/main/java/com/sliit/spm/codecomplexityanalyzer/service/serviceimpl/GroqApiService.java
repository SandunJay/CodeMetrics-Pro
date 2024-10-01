package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.spm.codecomplexityanalyzer.model.GeminiAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GroqApiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String COMPLETIONS_ENDPOINT = "/chat/completions";
    private static final String DEFAULT_COMMAND = "Get improved code for the following code:\\n"; // Properly escaped newline
    private static final String MODEL = "llama3-8b-8192";

    public GroqApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public GeminiAnalysisResponse analyzeCode(String code) throws Exception {
        String sanitizedCode = sanitizeInput(code); // Sanitize input to avoid invalid characters
        String response = sendPostRequest(COMPLETIONS_ENDPOINT, DEFAULT_COMMAND + sanitizedCode);
        return parseAnalysisResponse(response);
    }

    public String getCompletedCode(String code) throws Exception {
        String sanitizedCode = sanitizeInput(code);
        String response = sendPostRequest(COMPLETIONS_ENDPOINT, DEFAULT_COMMAND + sanitizedCode);
        return parseCompletionResponse(response);
    }

    public String getImprovedCode(String code) throws Exception {
        String sanitizedCode = sanitizeInput(code);
        String response = sendPostRequest(COMPLETIONS_ENDPOINT, DEFAULT_COMMAND + sanitizedCode);
        return parseImprovementResponse(response);
    }

    private String sendPostRequest(String endpoint, String content) throws Exception {
        String url = baseUrl + endpoint;
        String jsonBody = buildRequestBody(content);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, createHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private String buildRequestBody(String content) throws Exception {
        return String.format("{ \"messages\": [{ \"role\": \"user\", \"content\": \"%s\" }], \"model\": \"%s\" }", content, MODEL);
    }

    private String sanitizeInput(String code) {
        // Escape any backslashes, double quotes, and remove invalid characters
        return code.replace("\\", "\\\\")      // Escape backslashes
                .replace("\"", "\\\"")      // Escape double quotes
                .replace("\n", "\\n")       // Escape newline characters
                .replace("\r", "");         // Remove carriage return characters
    }

    private GeminiAnalysisResponse parseAnalysisResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            String overview = choices.get(0).path("message").path("content").asText();
            return new GeminiAnalysisResponse(overview);
        }
        return new GeminiAnalysisResponse("No analysis available");
    }

    private String parseCompletionResponse(String response) throws Exception {
        return parseMessageFromChoices(response, "No completion available");
    }

    private String parseImprovementResponse(String response) throws Exception {
        return parseMessageFromChoices(response, "No improvement suggestions available");
    }

    private String parseMessageFromChoices(String response, String defaultMsg) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            return choices.get(0).path("message").path("content").asText();
        }
        return defaultMsg;
    }
}
