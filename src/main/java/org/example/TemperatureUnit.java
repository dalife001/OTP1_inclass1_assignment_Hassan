package org.example;

import java.util.Objects;

public final class TemperatureUnit {
    private final int id;
    private final String unitKey;
    private final String name;
    private final String symbol;
    private final String description;

    public TemperatureUnit(int id, String name, String symbol) {
        this(id, name.toLowerCase(), name, symbol, "");
    }

    public TemperatureUnit(int id, String unitKey, String name, String symbol, String description) {
        this.id = id;
        this.unitKey = Objects.requireNonNull(unitKey, "unitKey");
        this.name = Objects.requireNonNull(name, "name");
        this.symbol = symbol;
        this.description = Objects.requireNonNull(description, "description");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnitKey() {
        return unitKey;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }
}
