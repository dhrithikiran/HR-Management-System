package com.hrms.onboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.onboarding.model.OnboardingCreateRequest;
import com.hrms.onboarding.model.OnboardingRecord;
import com.hrms.onboarding.service.OnboardingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OnboardingController implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingController.class);

    private final OnboardingService onboardingService;
    private final ObjectMapper objectMapper;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath(); // e.g. /onboarding, /onboarding/CND-001, /onboarding/ONB-001

        logger.info("Onboarding Service received {} {}", method, path);

        try {
            // Route /onboarding or /onboarding/
            if (path.equals("/onboarding") || path.equals("/onboarding/")) {
                if ("GET".equals(method)) {
                    handleListRecords(exchange);
                } else if ("POST".equals(method)) {
                    handleCreateRecord(exchange);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
                return;
            }

            // Sub-paths: /onboarding/{id}
            String subPath = path.substring("/onboarding/".length());
            String[] segments = subPath.split("/");

            if (segments.length == 1) {
                String lookupId = segments[0];
                if ("GET".equals(method)) {
                    handleGetRecord(exchange, lookupId);
                } else if ("DELETE".equals(method)) {
                    handleDeleteRecord(exchange, lookupId);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else {
                sendError(exchange, 404, "Endpoint Not Found: " + path);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request: {}", e.getMessage());
            sendError(exchange, 400, e.getMessage());
        } catch (NoSuchElementException e) {
            logger.warn("Not found: {}", e.getMessage());
            sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal server error handling {}: {}", path, e.getMessage(), e);
            sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleListRecords(HttpExchange exchange) throws IOException {
        List<OnboardingRecord> records = onboardingService.getAllRecords();
        sendJsonResponse(exchange, 200, records);
    }

    private void handleCreateRecord(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        OnboardingCreateRequest request;
        try {
            request = objectMapper.readValue(is, OnboardingCreateRequest.class);
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid JSON payload: " + e.getMessage());
            return;
        }

        if (request.getCandidateId() == null || request.getCandidateId().trim().isEmpty()) {
            sendError(exchange, 400, "Field 'candidateId' is required");
            return;
        }
        if (request.getCandidateName() == null || request.getCandidateName().trim().isEmpty()) {
            sendError(exchange, 400, "Field 'candidateName' is required");
            return;
        }

        OnboardingRecord created = onboardingService.createOnboardingRecord(request);
        sendJsonResponse(exchange, 201, created);
    }

    private void handleGetRecord(HttpExchange exchange, String lookupId) throws IOException {
        Optional<OnboardingRecord> optionalRecord = onboardingService.getRecordByCandidateOrOnboardingId(lookupId);
        if (optionalRecord.isEmpty()) {
            sendError(exchange, 404, "Onboarding record not found for ID: " + lookupId);
            return;
        }
        sendJsonResponse(exchange, 200, optionalRecord.get());
    }

    private void handleDeleteRecord(HttpExchange exchange, String id) throws IOException {
        boolean deleted = onboardingService.deleteRecord(id);
        if (deleted) {
            Map<String, Object> resp = Map.of(
                    "status", "SUCCESS",
                    "message", "Onboarding record deleted: " + id
            );
            sendJsonResponse(exchange, 200, resp);
        } else {
            sendError(exchange, 404, "Onboarding record not found: " + id);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = objectMapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> errorMap = Map.of(
                "error", message,
                "statusCode", statusCode,
                "timestamp", System.currentTimeMillis()
        );
        sendJsonResponse(exchange, statusCode, errorMap);
    }
}
