package com.yourname.myapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DatabaseConnection - JDBC-based database connection manager
 * 
 * Replaces HibernateUtil for direct JDBC connectivity
 * Provides centralized database connection management without ORM
 * 
 * Features:
 * - Simple connection pooling support
 * - Environment variable configuration
 * - SQL schema initialization on startup
 * - Clean database connection closing
 * 
 * Usage:
 * Connection conn = DatabaseConnection.getConnection();
 * // Use connection for JDBC operations
 * conn.close();
 * 
 * Or use try-with-resources:
 * try (Connection conn = DatabaseConnection.getConnection()) {
 *     // JDBC operations
 * }
 * 
 * @author OOAD Project
 * @since 2024
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    
    static {
        // Initialize database connection settings from config.properties or environment variables
        loadDatabaseConfiguration();
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully");
            
            // Initialize database schema on startup
            DatabaseInitializer.initializeDatabase(dbUrl, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load MySQL JDBC Driver", e);
            throw new ExceptionInInitializerError(e);
        }
    }
    
    /**
     * Load database configuration from config.properties with fallback to environment variables
     * Priority:
     * 1. config.properties file (for local development)
     * 2. Environment variables (for production/deployment)
     * 3. Hardcoded defaults (fallback only)
     */
    private static void loadDatabaseConfiguration() {
        Properties props = new Properties();
        boolean configFileFound = false;
        
        // Try to load config.properties from classpath
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                configFileFound = true;
                logger.info("Loaded database configuration from config.properties");
            }
        } catch (IOException e) {
            logger.warn("Could not load config.properties - falling back to environment variables", e);
        }
        
        // Load from properties file if found, otherwise from environment variables
        dbUrl = props.getProperty("db.url") != null ? props.getProperty("db.url") : 
                (System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/hr_ooad");
        
        dbUsername = props.getProperty("db.username") != null ? props.getProperty("db.username") : 
                     (System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "root");
        
        dbPassword = props.getProperty("db.password") != null ? props.getProperty("db.password") : 
                     (System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : null);
        
        if (dbPassword == null) {
            logger.error("Database password not found in config.properties or DB_PASSWORD environment variable!");
            throw new ExceptionInInitializerError(
                "Database password is required. Set DB_PASSWORD environment variable or add db.password to config.properties");
        }
        
        String configSource = configFileFound ? "config.properties" : "environment variables";
        logger.info("Database configuration loaded from: " + configSource);
    }
    
    /**
     * Get a new database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    
    /**
     * Get database URL
     * 
     * @return Database JDBC URL
     */
    public static String getDbUrl() {
        return dbUrl;
    }
    
    /**
     * Get database username
     * 
     * @return Database username
     */
    public static String getDbUsername() {
        return dbUsername;
    }
    
    /**
     * Get database password
     * 
     * @return Database password
     */
    public static String getDbPassword() {
        return dbPassword;
    }
}
