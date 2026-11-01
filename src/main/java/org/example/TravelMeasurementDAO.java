package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TravelMeasurementDAO {
    public void save(TravelMeasurement measurement) {
        try (Connection connection = DBConnection.getConnection()) {
            DatabaseSchema.initialize(connection);
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO travel_measurements
                        (distance, duration_seconds, speed, unit_id)
                    VALUES (?, ?, ?, ?)
                    """)) {
                statement.setDouble(1, measurement.distance());
                statement.setDouble(2, measurement.durationSeconds());
                statement.setDouble(3, measurement.speed());
                statement.setInt(4, measurement.unitId());
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save the travel measurement.", exception);
        }
    }
}
