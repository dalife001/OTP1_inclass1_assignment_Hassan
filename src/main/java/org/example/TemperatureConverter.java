package org.example;
public class TemperatureConverter {

    public double convert(double value, String from, String to) {
        String source = from.trim().toLowerCase();
        String target = to.trim().toLowerCase();
        if (source.equals(target)) {
            return value;
        }
        double celsius = switch (source) {
            case "celsius" -> value;
            case "fahrenheit" -> fahrenheitToCelsius(value);
            case "kelvin" -> kelvinToCelsius(value);
            default -> throw new IllegalArgumentException("Unsupported source unit: " + from);
        };
        return switch (target) {
            case "celsius" -> celsius;
            case "fahrenheit" -> celsiusToFahrenheit(celsius);
            case "kelvin" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Unsupported target unit: " + to);
        };
    }

    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    public double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public boolean isExtremeTemperature(double celsius) {
        return celsius < -40 || celsius > 50;
    }
}