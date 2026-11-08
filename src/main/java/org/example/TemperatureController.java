package org.example;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TemperatureController {
    @FXML private Label titleLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label languageLabel;
    @FXML private TextField inputField;
    @FXML private ComboBox<TemperatureUnit> fromUnit;
    @FXML private ComboBox<TemperatureUnit> toUnit;
    @FXML private ComboBox<LanguageOption> languageComboBox;
    @FXML private Button convertButton;
    @FXML private Label resultLabel;
    @FXML private ListView<String> historyList;
    @FXML private javafx.scene.layout.VBox root;

    private final Main application;
    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private ResourceBundle messages;
    private boolean databaseUnavailable;

    public TemperatureController(Main application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        messages = ResourceBundle.getBundle("messages", application.getLocale());
        configureUnits();
        configureLanguages();
        root.setNodeOrientation(application.getLocale().getLanguage().equals("ar")
                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
        resultLabel.setText(databaseUnavailable
                ? messages.getString("databaseUnavailable")
                : messages.getString("result"));
        inputField.setPromptText(messages.getString("enterTemperature"));
        loadHistory();
    }

    private void configureUnits() {
        List<TemperatureUnit> units;
        try {
            units = unitDAO.getAllUnits(application.getLocale());
        } catch (IllegalStateException exception) {
            units = TemperatureUnitDAO.defaultUnitsForLocale(application.getLocale());
            databaseUnavailable = true;
        }
        fromUnit.setItems(FXCollections.observableArrayList(units));
        toUnit.setItems(FXCollections.observableArrayList(units));
        if (!units.isEmpty()) {
            fromUnit.setValue(units.get(0));
            toUnit.setValue(units.size() > 1 ? units.get(1) : units.get(0));
        }
        StringConverter<TemperatureUnit> unitConverter = new StringConverter<>() {
            @Override
            public String toString(TemperatureUnit unit) {
                return unit == null ? "" : unit.getName() + " - " + unit.getDescription()
                        + " (" + unit.getSymbol() + ")";
            }

            @Override
            public TemperatureUnit fromString(String value) {
                return null;
            }
        };
        configureUnitComboBox(fromUnit, unitConverter);
        configureUnitComboBox(toUnit, unitConverter);
    }

    private void configureUnitComboBox(
            ComboBox<TemperatureUnit> comboBox, StringConverter<TemperatureUnit> converter) {
        comboBox.setConverter(converter);
        comboBox.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(TemperatureUnit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : converter.toString(item));
            }
        });
    }

    private void configureLanguages() {
        languageComboBox.setItems(FXCollections.observableArrayList(
                new LanguageOption("English", Locale.US),
                new LanguageOption("Suomi", new Locale("fi", "FI")),
                new LanguageOption("Svenska", new Locale("sv", "SE")),
                new LanguageOption("日本語", Locale.JAPAN),
                new LanguageOption("العربية", new Locale("ar", "SA"))));
        languageComboBox.setValue(languageComboBox.getItems().stream()
                .filter(option -> option.locale().equals(application.getLocale()))
                .findFirst()
                .orElse(languageComboBox.getItems().get(0)));
        languageComboBox.setOnAction(event -> {
            LanguageOption selected = languageComboBox.getValue();
            if (selected != null && !selected.locale().equals(application.getLocale())) {
                application.showLocale(selected.locale());
            }
        });
    }

    @FXML
    private void convert() {
        String value = inputField.getText() == null ? "" : inputField.getText().trim();
        if (value.isEmpty()) {
            resultLabel.setText(messages.getString("enterValue"));
            return;
        }
        if (fromUnit.getValue() == null || toUnit.getValue() == null) {
            resultLabel.setText(messages.getString("selectUnits"));
            return;
        }
        try {
            double input = Double.parseDouble(value);
            double output = converter.convert(input,
                    fromUnit.getValue().getUnitKey(), toUnit.getValue().getUnitKey());
            resultLabel.setText(String.format(application.getLocale(),
                    messages.getString("resultFormat"), output, toUnit.getValue().getName(),
                    toUnit.getValue().getSymbol()));
            try {
                recordDAO.saveRecord(new TempRecord(input, output,
                        fromUnit.getValue().getId(), toUnit.getValue().getId(),
                        application.getLocale().getLanguage()));
                loadHistory();
            } catch (IllegalStateException exception) {
                resultLabel.setText(resultLabel.getText() + " " + messages.getString("notSaved"));
            }

        } catch (NumberFormatException exception) {
            resultLabel.setText(messages.getString("invalidNumber"));
        }
    }

    private void loadHistory() {
        if (databaseUnavailable || historyList == null) {
            return;
        }
        try {
            historyList.getItems().setAll(recordDAO.findLatest(5).stream()
                    .map(record -> String.format(application.getLocale(), "%.2f -> %.2f (%s)",
                            record.getInputValue(), record.getOutputValue(), record.getLanguage()))
                    .toList());
        } catch (IllegalStateException exception) {
            databaseUnavailable = true;
        }
    }

    public record LanguageOption(String name, Locale locale) {
        @Override
        public String toString() {
            return name;
        }
    }
}
