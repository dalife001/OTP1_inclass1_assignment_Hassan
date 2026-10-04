package org.example;

import java.util.Objects;

public final class TemperatureUnit {
    private final int id;
    private final String name;
    private final String symbol;

    public TemperatureUnit(int id, String name, String symbol) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
