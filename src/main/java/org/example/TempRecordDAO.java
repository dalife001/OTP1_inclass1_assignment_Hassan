package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {
    public void saveRecord(TempRecord record) {
        try (Connection connection = DBConnection.getConnection()) {
            createTable(connection);
            String sql = """
                    INSERT INTO temp_records
                        (input_value, output_value, from_unit_id, to_unit_id, language)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, record.getInputValue());
                statement.setDouble(2, record.getOutputValue());
                statement.setInt(3, record.getFromUnitId());
                statement.setInt(4, record.getToUnitId());
                statement.setString(5, record.getLanguage());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save the conversion record.", exception);
        }
    }

    public List<TempRecord> findLatest(int limit) {
            if (limit <= 0) {
                throw new IllegalArgumentException("History limit must be positive.");
            }
            try (Connection connection = DBConnection.getConnection()) {
                DatabaseSchema.initialize(connection);
                List<TempRecord> records = new ArrayList<>();
                String sql = """
                        SELECT id, input_value, output_value, from_unit_id, to_unit_id, language, created_at
                        FROM temp_records ORDER BY created_at DESC, id DESC LIMIT ?
                        """;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, limit);
                    try (ResultSet results = statement.executeQuery()) {
                        while (results.next()) {
                            records.add(new TempRecord(
                                    results.getInt("id"),
                                    results.getDouble("input_value"),
                                    results.getDouble("output_value"),
                                    results.getInt("from_unit_id"),
                                    results.getInt("to_unit_id"),
                                    results.getString("language"),
                                    results.getObject("created_at", LocalDateTime.class)));
                        }
                    }
                }
                return records;
            } catch (SQLException exception) {
                throw new IllegalStateException("Unable to load conversion history.", exception);
            }
    }

    private void createTable(Connection connection) throws SQLException {
        DatabaseSchema.initialize(connection);
    }
}
