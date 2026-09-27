package org.example;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureConverterTest {
    TemperatureConverter converter = new TemperatureConverter();


    @Test
    void fahrenheitToCelsius() {
    assertEquals(0.0,converter.fahrenheitToCelsius(32),0.001);
    assertEquals(50.0,converter.fahrenheitToCelsius(122),0.001);
    assertEquals(-40.0,converter.fahrenheitToCelsius(-40),0.001);
    assertEquals(100.0,converter.fahrenheitToCelsius(212),0.001);
    
    }

    @Test
    void celsiusToFahrenheit() {
    assertEquals(32.0,converter.celsiusToFahrenheit(0),0.001);
    assertEquals(122.0,converter.celsiusToFahrenheit(50),0.001);
    assertEquals(-40.0,converter.celsiusToFahrenheit(-40),0.001);
    assertEquals(212.0,converter.celsiusToFahrenheit(100),0.001);


    }

   @Test
    void isExtremeTemperature() {
    assertTrue(converter.isExtremeTemperature(-50));  
    assertTrue(converter.isExtremeTemperature(60));  
    assertFalse(converter.isExtremeTemperature(-30)); 
    assertFalse(converter.isExtremeTemperature(20));   
}
    @Test
    void kelvinToCelsius(){
    assertEquals(converter.kelvinToCelsius(0), -273.15, 0.001);
    assertEquals(-273.15, converter.kelvinToCelsius(0), 0.001);
    assertEquals(0.0, converter.kelvinToCelsius(273.15), 0.001);
    assertEquals(100.0, converter.kelvinToCelsius(373.15), 0.001);
}
}