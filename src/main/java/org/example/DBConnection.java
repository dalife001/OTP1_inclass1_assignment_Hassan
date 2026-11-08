package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class DBConnection {
    private static final String DEFAULT_DATABASE_URL =
            "jdbc:postgresql://ep-falling-brook-b1j1uj88-pooler.c-5.eu-central-1.aws.neon.tech/"
                    + "neondb?sslmode=require&channel_binding=require";

    public static Connection getConnection() throws SQLException {
        String url = readSetting("TEMPERATURE_DB_URL", DEFAULT_DATABASE_URL);
        String user = readSetting("TEMPERATURE_DB_USER", null);
        String password = readSetting("TEMPERATURE_DB_PASSWORD", null);
        if (user == null || password == null) {
            throw new SQLException(
                    "Database credentials are missing. Set TEMPERATURE_DB_USER and "
                            + "TEMPERATURE_DB_PASSWORD.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    static String readSetting(String name, String fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            value = readFromDotEnv(name);
        }
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String readFromDotEnv(String name) {
        Path envFile = Path.of(".env");
        if (!Files.isRegularFile(envFile)) {
            return null;
        }
        try {
            List<String> lines = Files.readAllLines(envFile);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith(name + "=")) {
                    String value = trimmed.substring(name.length() + 1).trim();
                    if (value.length() >= 2
                            && ((value.startsWith("\"") && value.endsWith("\""))
                            || (value.startsWith("'") && value.endsWith("'")))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read database configuration from .env.", exception);
        }
        return null;
    }
}
