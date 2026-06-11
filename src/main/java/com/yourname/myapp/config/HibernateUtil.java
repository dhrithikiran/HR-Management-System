package com.yourname.myapp.config;

/**
 * @deprecated HibernateUtil has been removed. 
 * 
 * This class is no longer used. The project has been migrated from Hibernate to plain JDBC.
 * 
 * Use DatabaseConnection instead:
 * 
 * import com.yourname.myapp.config.DatabaseConnection;
 * 
 * try (Connection conn = DatabaseConnection.getConnection()) {
 *     // Use JDBC for database operations
 * }
 * 
 * All entity classes are now plain POJOs without JPA annotations.
 * Repositories use direct JDBC queries instead of Hibernate Session.
 * 
 * @see DatabaseConnection
 */
@Deprecated
public class HibernateUtil {
    
    /**
     * @deprecated Use DatabaseConnection.getConnection() instead
     */
    @Deprecated
    public static Object getSession() {
        throw new UnsupportedOperationException("HibernateUtil has been removed. Use DatabaseConnection.getConnection() for JDBC operations.");
    }
    
    /**
     * @deprecated Use DatabaseConnection instead
     */
    @Deprecated
    public static Object getSessionFactory() {
        throw new UnsupportedOperationException("HibernateUtil has been removed. Use DatabaseConnection.getConnection() for JDBC operations.");
    }
    
    /**
     * @deprecated No longer needed with JDBC connections
     */
    @Deprecated
    public static void closeSessionFactory() {
        // JDBC connections close automatically with try-with-resources
    }
}
