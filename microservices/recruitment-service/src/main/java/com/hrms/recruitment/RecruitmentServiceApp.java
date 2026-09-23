package com.hrms.recruitment;

import com.hrms.recruitment.client.OnboardingServiceClient;
import com.hrms.recruitment.controller.CandidateController;
import com.hrms.recruitment.controller.HealthController;
import com.hrms.recruitment.repository.CandidateRepository;
import com.hrms.recruitment.repository.CandidateRepositoryImpl;
import com.hrms.recruitment.service.CandidateService;
import com.hrms.recruitment.service.CandidateServiceImpl;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Standalone Microservice entry point for Recruitment Service.
 * Listens on HTTP port 8081.
 */
public class RecruitmentServiceApp {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentServiceApp.class);
    private static final int DEFAULT_PORT = 8081;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (System.getenv("PORT") != null) {
            try {
                port = Integer.parseInt(System.getenv("PORT"));
            } catch (NumberFormatException ignored) {}
        }

        try {
            // Initialize dependencies
            CandidateRepository candidateRepository = new CandidateRepositoryImpl();
            OnboardingServiceClient onboardingServiceClient = new OnboardingServiceClient();
            CandidateService candidateService = new CandidateServiceImpl(candidateRepository, onboardingServiceClient);

            // Create HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Register HTTP Controllers
            server.createContext("/health", new HealthController());
            server.createContext("/candidates", new CandidateController(candidateService));

            server.start();
            logger.info("=================================================================");
            logger.info("  RECRUITMENT MICROSERVICE started successfully on port {}", port);
            logger.info("  Health Check: http://localhost:{}/health", port);
            logger.info("  Candidates API: http://localhost:{}/candidates", port);
            logger.info("=================================================================");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Recruitment Microservice...");
                server.stop(1);
            }));

        } catch (IOException e) {
            logger.error("Failed to start Recruitment Microservice on port {}", port, e);
            System.exit(1);
        }
    }
}
