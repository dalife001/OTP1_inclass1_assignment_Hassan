package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TempRecordDAO {
    public void saveRecord(TempRecord record) {
        try (Connection connection = DBConnection.getConnection()) {
            createTable(connection);
            String sql = """
                    INSERT INTO temp_records
                        (input_value, output_value, from_unit_id, to_unit_id)
                    VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, record.getInputValue());
                statement.setDouble(2, record.getOutputValue());
                statement.setInt(3, record.getFromUnitId());
                statement.setInt(4, record.getToUnitId());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save the conversion record.", exception);
        }
    }

    private void createTable(Connection connection) throws SQLException {
        DatabaseSchema.initialize(connection);
    }
}
