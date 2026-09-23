package com.hrms.recruitment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.recruitment.model.Candidate;
import com.hrms.recruitment.model.CandidateRequest;
import com.hrms.recruitment.model.CandidateSelectionResponse;
import com.hrms.recruitment.service.CandidateService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CandidateController implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;
    private final ObjectMapper objectMapper;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath(); // e.g. /candidates, /candidates/CND-001, /candidates/CND-001/select

        logger.info("Recruitment Service received {} {}", method, path);

        try {
            // Route /candidates or /candidates/
            if (path.equals("/candidates") || path.equals("/candidates/")) {
                if ("GET".equals(method)) {
                    handleListCandidates(exchange);
                } else if ("POST".equals(method)) {
                    handleCreateCandidate(exchange);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
                return;
            }

            // Sub-paths: /candidates/{id} or /candidates/{id}/select
            String subPath = path.substring("/candidates/".length());
            String[] segments = subPath.split("/");

            if (segments.length == 1) {
                String candidateId = segments[0];
                if ("GET".equals(method)) {
                    handleGetCandidate(exchange, candidateId);
                } else if ("DELETE".equals(method)) {
                    handleDeleteCandidate(exchange, candidateId);
                } else {
                    sendError(exchange, 405, "Method Not Allowed");
                }
            } else if (segments.length == 2 && "select".equalsIgnoreCase(segments[1])) {
                String candidateId = segments[0];
                if ("POST".equals(method)) {
                    handleSelectCandidate(exchange, candidateId);
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

    private void handleListCandidates(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String status = null;
        if (query != null && query.contains("status=")) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "status".equalsIgnoreCase(pair[0])) {
                    status = pair[1];
                }
            }
        }

        List<Candidate> candidates = candidateService.getAllCandidates(status);
        sendJsonResponse(exchange, 200, candidates);
    }

    private void handleCreateCandidate(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        CandidateRequest request;
        try {
            request = objectMapper.readValue(is, CandidateRequest.class);
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid JSON payload: " + e.getMessage());
            return;
        }

        if (request.getCandidateName() == null || request.getCandidateName().trim().isEmpty()) {
            sendError(exchange, 400, "Field 'candidateName' is required");
            return;
        }
        if (request.getContactInfo() == null || request.getContactInfo().trim().isEmpty()) {
            sendError(exchange, 400, "Field 'contactInfo' is required");
            return;
        }

        Candidate created = candidateService.createCandidate(request);
        sendJsonResponse(exchange, 201, created);
    }

    private void handleGetCandidate(HttpExchange exchange, String candidateId) throws IOException {
        Optional<Candidate> optionalCandidate = candidateService.getCandidateById(candidateId);
        if (optionalCandidate.isEmpty()) {
            sendError(exchange, 404, "Candidate not found: " + candidateId);
            return;
        }
        sendJsonResponse(exchange, 200, optionalCandidate.get());
    }

    private void handleSelectCandidate(HttpExchange exchange, String candidateId) throws IOException {
        CandidateSelectionResponse response = candidateService.selectCandidate(candidateId);
        if (response.isOnboardingInitiated()) {
            sendJsonResponse(exchange, 200, response);
        } else {
            // Onboarding service failed -> return 503 Service Unavailable with clear failure details
            sendJsonResponse(exchange, 503, response);
        }
    }

    private void handleDeleteCandidate(HttpExchange exchange, String candidateId) throws IOException {
        boolean deleted = candidateService.deleteCandidate(candidateId);
        if (deleted) {
            Map<String, Object> resp = Map.of(
                    "status", "SUCCESS",
                    "message", "Candidate deleted: " + candidateId
            );
            sendJsonResponse(exchange, 200, resp);
        } else {
            sendError(exchange, 404, "Candidate not found: " + candidateId);
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
