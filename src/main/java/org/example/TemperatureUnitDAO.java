package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TemperatureUnitDAO {
    private static final List<TemperatureUnit> DEFAULT_UNITS = List.of(
            new TemperatureUnit(1, "celsius", "Celsius", "°C", "Temperature scale"),
            new TemperatureUnit(2, "fahrenheit", "Fahrenheit", "°F", "Temperature scale"),
            new TemperatureUnit(3, "kelvin", "Kelvin", "K", "Absolute temperature scale")
    );

    public List<TemperatureUnit> getAllUnits() {
        return getAllUnits(Locale.ENGLISH);
    }

    public List<TemperatureUnit> getAllUnits(Locale locale) {
        try (Connection connection = DBConnection.getConnection()) {
            createTable(connection);
            seedDefaults(connection);
            return readUnits(connection, locale == null ? "en" : locale.getLanguage());
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to load temperature units. Configure TEMPERATURE_DB_URL, "
                            + "TEMPERATURE_DB_USER, and TEMPERATURE_DB_PASSWORD.",
                    exception);
        }
    }

    private void createTable(Connection connection) throws SQLException {
        DatabaseSchema.initialize(connection);
    }

    private void seedDefaults(Connection connection) throws SQLException {
        String sql = "INSERT INTO units (unit_key, category, symbol) VALUES (?, 'temperature', ?) "
                + "ON CONFLICT (unit_key) DO NOTHING";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (TemperatureUnit unit : DEFAULT_UNITS) {
                statement.setString(1, unit.getUnitKey());
                statement.setString(2, unit.getSymbol());
                statement.addBatch();
            }
            statement.executeBatch();
        }
        String translationSql = """
                INSERT INTO unit_translations (unit_id, lang, name, description)
                SELECT id, ?, ?, ? FROM units WHERE unit_key = ?
                ON CONFLICT (unit_id, lang) DO NOTHING
                """;
        try (PreparedStatement statement = connection.prepareStatement(translationSql)) {
            for (TemperatureUnit unit : DEFAULT_UNITS) {
                for (String language : List.of("en", "fi", "sv", "ja", "ar")) {
                    statement.setString(1, language);
                    statement.setString(2, localizedName(unit.getUnitKey(), language));
                    statement.setString(3, localizedDescription(unit.getUnitKey(), language));
                    statement.setString(4, unit.getUnitKey());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }

    private List<TemperatureUnit> readUnits(Connection connection, String language) throws SQLException {
        List<TemperatureUnit> units = new ArrayList<>();
        String sql = """
                SELECT u.id, u.unit_key, u.symbol,
                       COALESCE(t.name, fallback.name) AS name,
                       COALESCE(t.description, fallback.description) AS description
                FROM units u
                JOIN unit_translations fallback ON fallback.unit_id = u.id AND fallback.lang = 'en'
                LEFT JOIN unit_translations t ON t.unit_id = u.id AND t.lang = ?
                WHERE u.category = 'temperature'
                ORDER BY u.id
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, language);
            try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                units.add(new TemperatureUnit(
                        results.getInt("id"),
                        results.getString("unit_key"),
                        results.getString("name"),
                        results.getString("symbol"),
                        results.getString("description")));
            }
            }
        }
        return units;
    }

    static String localizedName(String key, String language) {
        return switch (language + ":" + key) {
            case "fi:celsius" -> "Celsius";
            case "sv:celsius" -> "Celsius";
            case "ja:celsius" -> "摂氏";
            case "ar:celsius" -> "مئوية";
            case "fi:fahrenheit" -> "Fahrenheit";
            case "sv:fahrenheit" -> "Fahrenheit";
            case "ja:fahrenheit" -> "華氏";
            case "ar:fahrenheit" -> "فهرنهايت";
            case "fi:kelvin" -> "Kelvin";
            case "sv:kelvin" -> "Kelvin";
            case "ja:kelvin" -> "ケルビン";
            case "ar:kelvin" -> "كلفن";
            default -> DEFAULT_UNITS.stream().filter(unit -> unit.getUnitKey().equals(key))
                    .findFirst().map(TemperatureUnit::getName).orElse(key);
        };
    }

    static String localizedDescription(String key, String language) {
        if ("ja".equals(language)) {
            return "温度の単位";
        }
        if ("ar".equals(language)) {
            return "وحدة قياس الحرارة";
        }
        if ("fi".equals(language)) {
            return "Lämpötilayksikkö";
        }
        if ("sv".equals(language)) {
            return "Temperaturenhet";
        }
        return DEFAULT_UNITS.stream().filter(unit -> unit.getUnitKey().equals(key))
                .findFirst().map(TemperatureUnit::getDescription).orElse("Temperature unit");
    }

    static List<TemperatureUnit> defaultUnitsForTests() {
        return DEFAULT_UNITS;
    }

    static List<TemperatureUnit> defaultUnitsForLocale(Locale locale) {
        String language = locale == null ? "en" : locale.getLanguage();
        return DEFAULT_UNITS.stream()
                .map(unit -> new TemperatureUnit(unit.getId(), unit.getUnitKey(),
                        localizedName(unit.getUnitKey(), language), unit.getSymbol(),
                        localizedDescription(unit.getUnitKey(), language)))
                .toList();
    }
}
