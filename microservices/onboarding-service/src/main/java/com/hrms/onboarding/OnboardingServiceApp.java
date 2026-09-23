package com.hrms.onboarding;

import com.hrms.onboarding.controller.HealthController;
import com.hrms.onboarding.controller.OnboardingController;
import com.hrms.onboarding.repository.OnboardingRepository;
import com.hrms.onboarding.repository.OnboardingRepositoryImpl;
import com.hrms.onboarding.service.OnboardingService;
import com.hrms.onboarding.service.OnboardingServiceImpl;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Standalone Microservice entry point for Onboarding Service.
 * Listens on HTTP port 8082.
 */
public class OnboardingServiceApp {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingServiceApp.class);
    private static final int DEFAULT_PORT = 8082;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (System.getenv("PORT") != null) {
            try {
                port = Integer.parseInt(System.getenv("PORT"));
            } catch (NumberFormatException ignored) {}
        }

        try {
            // Initialize dependencies (Strictly decoupled from Recruitment domain)
            OnboardingRepository onboardingRepository = new OnboardingRepositoryImpl();
            OnboardingService onboardingService = new OnboardingServiceImpl(onboardingRepository);

            // Create HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Register HTTP Controllers
            server.createContext("/health", new HealthController());
            server.createContext("/onboarding", new OnboardingController(onboardingService));

            server.start();
            logger.info("=================================================================");
            logger.info("  ONBOARDING MICROSERVICE started successfully on port {}", port);
            logger.info("  Health Check: http://localhost:{}/health", port);
            logger.info("  Onboarding API: http://localhost:{}/onboarding", port);
            logger.info("=================================================================");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Onboarding Microservice...");
                server.stop(1);
            }));

        } catch (IOException e) {
            logger.error("Failed to start Onboarding Microservice on port {}", port, e);
            System.exit(1);
        }
    }
}
