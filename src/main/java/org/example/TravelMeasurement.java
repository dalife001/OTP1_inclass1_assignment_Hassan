package org.example;

public record TravelMeasurement(double distance, double durationSeconds, double speed, int unitId) {
    public TravelMeasurement {
        if (distance < 0 || durationSeconds <= 0 || speed < 0) {
            throw new IllegalArgumentException("Distance and speed must be non-negative; duration must be positive.");
        }
    }
}
