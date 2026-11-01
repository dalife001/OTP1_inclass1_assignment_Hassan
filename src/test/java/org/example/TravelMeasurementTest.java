package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TravelMeasurementTest {
    @Test
    void storesDistanceDurationSpeedAndUnit() {
        TravelMeasurement measurement = new TravelMeasurement(100, 20, 5, 1);

        assertEquals(100, measurement.distance());
        assertEquals(20, measurement.durationSeconds());
        assertEquals(5, measurement.speed());
        assertEquals(1, measurement.unitId());
    }

    @Test
    void rejectsInvalidTravelValues() {
        assertThrows(IllegalArgumentException.class, () -> new TravelMeasurement(-1, 10, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new TravelMeasurement(1, 0, 1, 1));
    }
}
