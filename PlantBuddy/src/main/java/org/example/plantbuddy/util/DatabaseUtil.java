package org.example.plantbuddy.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    static {
        try {
            loadDatabaseProperties();
        } catch (IOException e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads database properties from application.properties file
     */
    private static void loadDatabaseProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);

            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");

            // Verify that all required properties are present
            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IOException("Database connection properties missing in application.properties");
            }
        }
    }

    /**
     * Gets a database connection using properties from application.properties
     * @return A Connection to the database
     * @throws SQLException If connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}