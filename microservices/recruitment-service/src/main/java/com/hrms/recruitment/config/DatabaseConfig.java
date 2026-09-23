package com.hrms.recruitment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/hr_ooad?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASS;

    static {
        DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DEFAULT_URL;
        DB_USER = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : DEFAULT_USER;
        DB_PASS = System.getenv("DB_PASSWORD");

        if (DB_PASS == null || DB_PASS.trim().isEmpty()) {
            logger.error("Recruitment Service: DB_PASSWORD environment variable is missing!");
            throw new IllegalStateException("Database password is required. Please set the DB_PASSWORD environment variable.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("Recruitment Service: MySQL JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            logger.error("Recruitment Service: MySQL Driver class not found", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
