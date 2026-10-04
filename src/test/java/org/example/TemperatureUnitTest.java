package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemperatureUnitTest {
    @Test
    void storesUnitDetails() {
        TemperatureUnit unit = new TemperatureUnit(7, "Celsius", "°C");

        assertEquals(7, unit.getId());
        assertEquals("Celsius", unit.getName());
        assertEquals("°C", unit.getSymbol());
    }

    @Test
    void rejectsMissingName() {
        assertThrows(NullPointerException.class, () -> new TemperatureUnit(1, null, "x"));
    }
}
