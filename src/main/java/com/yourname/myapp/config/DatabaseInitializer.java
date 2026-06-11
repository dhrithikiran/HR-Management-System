package com.yourname.myapp.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DatabaseInitializer - Handles SQL schema initialization
 * 
 * Purpose:
 * - Execute SQL schema files to initialize database tables
 * - Replace Hibernate's auto-schema generation (hbm2ddl.auto)
 * - Provide centralized database setup on application startup
 * 
 * Features:
 * - Loads schema.sql from classpath (src/main/resources/schema.sql)
 * - Checks if tables exist before initialization
 * - Executes SQL statements safely with error handling
 * - Provides logging for debugging
 * 
 * Usage:
 * Call DatabaseInitializer.initializeDatabase() in HibernateUtil before creating SessionFactory
 * 
 * @author OOAD Project
 * @since 2024
 */
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String SCHEMA_RESOURCE = "/schema.sql";

    /**
     * Initialize database by executing schema.sql if tables don't exist
     * 
     * @param url Database JDBC URL
     * @param username Database username
     * @param password Database password
     */
    public static void initializeDatabase(String url, String username, String password) {
        try {
            // First, load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Check if tables already exist
            if (tablesExist(url, username, password)) {
                logger.info("Database tables already exist. Skipping schema initialization.");
                return;
            }
            
            logger.info("Database tables not found. Initializing schema from schema.sql...");
            
            // Read and execute schema.sql
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                executeSchemaScript(conn);
                logger.info("Database schema initialized successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found. Make sure mysql-connector-j dependency is in pom.xml", e);
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    /**
     * Check if database tables already exist
     * 
     * @param url Database JDBC URL
     * @param username Database username
     * @param password Database password
     * @return true if tables exist, false otherwise
     */
    private static boolean tablesExist(String url, String username, String password) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            // Check for the existence of the main employee table
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'employees'");
            if (rs.next() && rs.getInt(1) > 0) {
                logger.debug("Employee table exists in database");
                return true;
            }
        } catch (SQLException e) {
            logger.debug("Error checking for existing tables (this is normal on fresh database): {}", e.getMessage());
        }
        return false;
    }

    /**
     * Execute SQL schema script from classpath resource
     * 
     * @param conn Database connection
     * @throws SQLException if SQL execution fails
     */
    private static void executeSchemaScript(Connection conn) throws SQLException {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (is == null) {
                throw new RuntimeException("Schema resource not found: " + SCHEMA_RESOURCE);
            }
            
            String schema = readInputStream(is);
            executeMultipleStatements(conn, schema);
            
        } catch (IOException e) {
            throw new SQLException("Failed to read schema resource", e);
        }
    }

    /**
     * Read input stream to string
     * 
     * @param is Input stream to read
     * @return String content of input stream
     * @throws IOException if reading fails
     */
    private static String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comments and empty lines
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Execute multiple SQL statements from a script
     * Statements are separated by semicolons (;)
     * 
     * @param conn Database connection
     * @param script SQL script with multiple statements
     * @throws SQLException if execution fails
     */
    private static void executeMultipleStatements(Connection conn, String script) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Split by semicolon to get individual statements
            String[] statements = script.split(";");
            
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        logger.debug("Executing SQL: {}", trimmed.substring(0, Math.min(80, trimmed.length())) + "...");
                        stmt.execute(trimmed);
                    } catch (SQLException e) {
                        logger.error("Error executing statement: {}", trimmed, e);
                        // Log but continue with next statement (some tables might already exist)
                        // Only throw if it's a critical error
                        if (e.getMessage().contains("Syntax error")) {
                            throw e;
                        }
                    }
                }
            }
        }
    }
}
