package org.example;

public class Main {

    public static void main(String[] args) {

        TemperatureConverter converter = new TemperatureConverter();

        System.out.println("Temperature Converter");

        System.out.println("100 Kelvin = "
                + converter.kelvinToCelsius(100)
                + " Celsius");

        System.out.println("32 Fahrenheit = "
                + converter.fahrenheitToCelsius(32)
                + " Celsius");

        System.out.println("0 Celsius = "
                + converter.celsiusToFahrenheit(0)
                + " Fahrenheit");
    }
}