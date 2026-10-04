package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_DATABASE_URL = "jdbc:postgresql://localhost:5432/temperature";

    public static Connection getConnection() throws SQLException {
        String url = readSetting("TEMPERATURE_DB_URL", DEFAULT_DATABASE_URL);
        String user = System.getenv("TEMPERATURE_DB_USER");
        String password = System.getenv("TEMPERATURE_DB_PASSWORD");
        if (user == null || password == null) {
            throw new SQLException(
                    "Database credentials are missing. Set TEMPERATURE_DB_USER and "
                            + "TEMPERATURE_DB_PASSWORD.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    static String readSetting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
