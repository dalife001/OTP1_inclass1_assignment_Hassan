package org.example;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureUnitDAOTest {
    @Test
    void localizesDefaultUnitsForJapanese() {
        assertEquals("摂氏", TemperatureUnitDAO.defaultUnitsForLocale(Locale.JAPAN).get(0).getName());
    }

    @Test
    void unknownLanguageFallsBackToEnglish() {
        assertEquals("Celsius", TemperatureUnitDAO.localizedName("celsius", "de"));
        assertEquals("Temperature scale",
                TemperatureUnitDAO.localizedDescription("celsius", "de"));
    }
}
