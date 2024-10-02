package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.spm.codecomplexityanalyzer.model.AiAnalysisResponse;
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
    private static final String IMPROVE_COMMAND = "Get improved code for the following code:\\n";
    private static final String ANALYZE_COMMAND = "Analyze the below provided code:\\n";
    private static final String COMPLETE_COMMAND = "If the below given code is incomplete, complete it and give the completed sourcecode. Without any additional words or text or instructions. just the completed source code :\\n";
    private static final String MODEL = "llama3-8b-8192";

    public GroqApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public AiAnalysisResponse analyzeCode(String code) throws Exception {
        String sanitizedCode = sanitizeInput(code);
        String overview = sendPostRequest(COMPLETIONS_ENDPOINT, ANALYZE_COMMAND + sanitizedCode);
        String completedCode = sendPostRequest(COMPLETIONS_ENDPOINT, COMPLETE_COMMAND + sanitizedCode);
        String improvedCode = sendPostRequest(COMPLETIONS_ENDPOINT, IMPROVE_COMMAND + sanitizedCode);

        return AiAnalysisResponse.builder()
                .overview(parseResponse(overview))
                .completedCode(parseResponse(completedCode))
                .improvedCode(parseResponse(improvedCode))
                .build();
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
        return code.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String parseResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            return choices.get(0).path("message").path("content").asText();
        }
        return "No response available";
    }

}
