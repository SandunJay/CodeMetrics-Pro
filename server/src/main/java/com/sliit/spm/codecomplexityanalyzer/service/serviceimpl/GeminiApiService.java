//package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sliit.spm.codecomplexityanalyzer.model.GeminiAnalysisResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class GeminiApiService {
//    @Value("${gemini.api.key}") // Inject your API key from application properties
//    private String apiKey;
//
//    @Value("${gemini.api.base.url}") // Inject base URL of Google Gemini API
//    private String baseUrl;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public GeminiApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
//        this.restTemplate = restTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    // Analyze the code with Gemini API to get AI overview
//    public GeminiAnalysisResponse analyzeCode(String code) throws Exception {
//        String url = baseUrl + "/analyze"; // Replace with actual Gemini endpoint
//        HttpEntity<String> request = new HttpEntity<>(code, createHeaders());
//        String response = restTemplate.postForObject(url, request, String.class);
//        return parseAnalysisResponse(response);
//    }
//
//    // Get completed code if the code is incomplete
//    public String getCompletedCode(String code) throws Exception {
//        String url = baseUrl + "/complete"; // Replace with actual Gemini endpoint
//        HttpEntity<String> request = new HttpEntity<>(code, createHeaders());
//        String response = restTemplate.postForObject(url, request, String.class);
//        return parseCompletionResponse(response);
//    }
//
//    // Get improved code suggestions from Gemini
//    public String getImprovedCode(String code) throws Exception {
//        String url = baseUrl + "/improve"; // Replace with actual Gemini endpoint
//        HttpEntity<String> request = new HttpEntity<>(code, createHeaders());
//        String response = restTemplate.postForObject(url, request, String.class);
//        return parseImprovementResponse(response);
//    }
//
//    // Create headers for authentication
//    private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiKey); // Bearer token authentication
//        headers.set("Content-Type", "application/json");
//        return headers;
//    }
//
//    // Parse the AI analysis response into a GeminiAnalysisResponse object
//    private GeminiAnalysisResponse parseAnalysisResponse(String response) throws Exception {
//        JsonNode root = objectMapper.readTree(response);
//        String overview = root.path("overview").asText();
//        return new GeminiAnalysisResponse(overview);
//    }
//
//    // Parse code completion response
//    private String parseCompletionResponse(String response) throws Exception {
//        JsonNode root = objectMapper.readTree(response);
//        return root.path("completedCode").asText();
//    }
//
//    // Parse code improvement response
//    private String parseImprovementResponse(String response) throws Exception {
//        JsonNode root = objectMapper.readTree(response);
//        return root.path("improvedCode").asText();
//    }
//}
