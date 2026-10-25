package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    private static final String BUNDLE_NAME = "MessagesBundle";
    private static ResourceBundle messages = ResourceBundle.getBundle(BUNDLE_NAME, Locale.US);
    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();

    @Override
    public void start(Stage stage) {

        Label title = new Label(message("title"));

        // Temperature input
        Label inputLabel = new Label(message("temperature"));
        TextField inputField = new TextField();
        inputField.setPromptText(message("enterTemperature"));

        // From unit
        Label fromLabel = new Label(message("from"));
        ComboBox<TemperatureUnit> fromUnit = new ComboBox<>();
        fromUnit.setPrefWidth(250);

        // To unit
        Label toLabel = new Label(message("to"));
        ComboBox<TemperatureUnit> toUnit = new ComboBox<>();
        toUnit.setPrefWidth(250);

        // Configure ComboBox display strings (shows Name and Symbol)
        configureComboBox(fromUnit);
        configureComboBox(toUnit);

        // Load units from Database
        boolean databaseAvailable = true;
        List<TemperatureUnit> units;
        try {
            units = unitDAO.getAllUnits();
        } catch (IllegalStateException exception) {
            databaseAvailable = false;
            units = TemperatureUnitDAO.defaultUnitsForTests();
        }
        if (units != null && !units.isEmpty()) {
            fromUnit.getItems().addAll(units);
            toUnit.getItems().addAll(units);

            fromUnit.setValue(units.get(0));
            if (units.size() > 1) {
                toUnit.setValue(units.get(1));
            } else {
                toUnit.setValue(units.get(0));
            }
        }

        // Convert button
        Button convertButton = new Button(message("convert"));

        // Result
        Label resultLabel = new Label(databaseAvailable
                ? message("result")
                : message("databaseUnavailable"));

        // Button action
        convertButton.setOnAction(event -> {
            try {
                if (inputField.getText() == null || inputField.getText().trim().isEmpty()) {
                    resultLabel.setText(message("enterValue"));
                    return;
                }

                double temperature = Double.parseDouble(inputField.getText().trim());
                TemperatureUnit from = fromUnit.getValue();
                TemperatureUnit to = toUnit.getValue();

                if (from == null || to == null) {
                    resultLabel.setText(message("selectUnits"));
                    return;
                }

                double result = convertTemperature(
                        temperature,
                        from.getName(),
                        to.getName()
                );

                resultLabel.setText(
                        String.format(message("resultFormat"), result,
                                to.getSymbol() != null ? to.getSymbol() : "")
                );

                // Save conversion record
                TempRecord record = new TempRecord(
                        temperature,
                        result,
                        from.getId(),
                        to.getId()
                );
                try {
                    recordDAO.saveRecord(record);
                } catch (IllegalStateException exception) {
                    resultLabel.setText(resultLabel.getText() + " " + message("notSaved"));
                }

            } catch (NumberFormatException e) {
                resultLabel.setText(message("invalidNumber"));
            } catch (Exception e) {
                resultLabel.setText(message("conversionError"));
                e.printStackTrace();
            }
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                title,
                inputLabel,
                inputField,
                fromLabel,
                fromUnit,
                toLabel,
                toUnit,
                convertButton,
                resultLabel
        );

        Scene scene = new Scene(layout, 400, 450);
        stage.setTitle("Temperature Converter");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Formats how TemperatureUnit objects are displayed in the ComboBox dropdown and selected value.
     */
    private void configureComboBox(ComboBox<TemperatureUnit> comboBox) {
        StringConverter<TemperatureUnit> converter = new StringConverter<>() {
            @Override
            public String toString(TemperatureUnit unit) {
                if (unit == null) return "";
                return unit.getSymbol() != null && !unit.getSymbol().isEmpty()
                        ? String.format("%s (%s)", unit.getName(), unit.getSymbol())
                        : unit.getName();
            }

            @Override
            public TemperatureUnit fromString(String string) {
                return null; // Not needed for read-only ComboBoxes
            }
        };

        comboBox.setConverter(converter);
        comboBox.setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(TemperatureUnit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(converter.toString(item));
                }
            }
        });
    }

    private double convertTemperature(double temperature, String from, String to) {
        if (from == null || to == null) return temperature;

        String fromLower = from.trim().toLowerCase();
        String toLower = to.trim().toLowerCase();

        if (fromLower.equals(toLower)) {
            return temperature;
        }

        if (fromLower.equals("celsius") && toLower.equals("fahrenheit")) {
            return converter.celsiusToFahrenheit(temperature);
        } else if (fromLower.equals("fahrenheit") && toLower.equals("celsius")) {
            return converter.fahrenheitToCelsius(temperature);
        } else if (fromLower.equals("kelvin") && toLower.equals("celsius")) {
            return converter.kelvinToCelsius(temperature);
        } else if (fromLower.equals("celsius") && toLower.equals("kelvin")) {
            return temperature + 273.15;
        } else if (fromLower.equals("fahrenheit") && toLower.equals("kelvin")) {
            double celsius = converter.fahrenheitToCelsius(temperature);
            return celsius + 273.15;
        } else if (fromLower.equals("kelvin") && toLower.equals("fahrenheit")) {
            double celsius = converter.kelvinToCelsius(temperature);
            return converter.celsiusToFahrenheit(celsius);
        }

        return temperature;
    }

    public static void main(String[] args) {
        messages = ResourceBundle.getBundle(BUNDLE_NAME, chooseLocale(args));
        launch(args);
    }

    private static String message(String key) {
        return messages.getString(key);
    }

    private static Locale chooseLocale(String[] args) {
        for (String argument : args) {
            if (argument.startsWith("--locale=")) {
                return localeFromTag(argument.substring("--locale=".length()));
            }
        }

        System.out.println("Select language / Valitse kieli / Välj språk / 言語を選択:");
        System.out.println("1. English");
        System.out.println("2. Finnish");
        System.out.println("3. Swedish");
        System.out.println("4. Japanese");
        System.out.print("Choice (default 1): ");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine();
            return switch (choice == null ? "" : choice.trim()) {
                case "2" -> new Locale("fi", "FI");
                case "3" -> new Locale("sv", "SE");
                case "4" -> Locale.JAPAN;
                default -> Locale.US;
            };
        } catch (IOException exception) {
            System.err.println("Unable to read language choice; using English.");
            return Locale.US;
        }
    }

    private static Locale localeFromTag(String tag) {
        Locale locale = Locale.forLanguageTag(tag.replace('_', '-'));
        return locale.getLanguage().isEmpty() ? Locale.US : locale;
    }
}