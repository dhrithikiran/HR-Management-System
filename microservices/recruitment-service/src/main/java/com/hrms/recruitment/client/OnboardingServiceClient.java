package com.hrms.recruitment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.recruitment.model.OnboardingPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP Client for inter-service communication from Recruitment Service to Onboarding Service.
 * Implements REST calls over network socket (port 8082).
 */
public class OnboardingServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingServiceClient.class);
    private static final String DEFAULT_BASE_URL = "http://localhost:8082";

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OnboardingServiceClient() {
        this(System.getenv("ONBOARDING_SERVICE_URL") != null ? System.getenv("ONBOARDING_SERVICE_URL") : DEFAULT_BASE_URL);
    }

    public OnboardingServiceClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends HTTP POST request to Onboarding Service to initiate onboarding for a selected candidate.
     * 
     * @param payload Candidate info required for onboarding
     * @return Onboarding record response from Onboarding Service
     * @throws Exception If Onboarding Service is unreachable or returns error
     */
    public Map<String, Object> initiateOnboarding(OnboardingPayload payload) throws Exception {
        String url = baseUrl + "/onboarding";
        String requestJson = objectMapper.writeValueAsString(payload);

        logger.info("Sending HTTP POST to Onboarding Service at {} with payload: {}", url, requestJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            logger.info("Received HTTP {} from Onboarding Service: {}", statusCode, responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);
                return resultMap;
            } else {
                throw new RuntimeException("Onboarding Service returned HTTP " + statusCode + ": " + responseBody);
            }
        } catch (java.io.IOException | InterruptedException e) {
            logger.error("HTTP connection failed to Onboarding Service at {}: {}", url, e.getMessage());
            throw new RuntimeException("Onboarding Service unreachable at " + url + " (" + e.getMessage() + ")", e);
        }
    }
}
