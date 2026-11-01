package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    private static final String BUNDLE_NAME = "MessagesBundle";
    private Stage stage;
    private Locale locale = Locale.US;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        showLocale(locale);
        stage.show();
    }

    void showLocale(Locale selectedLocale) {
        locale = selectedLocale;
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/Main.fxml"), bundle);
            loader.setControllerFactory(type -> {
                if (type == TemperatureController.class) {
                    return new TemperatureController(this);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException("Unable to create FXML controller.", exception);
                }
            });
            Parent root = loader.load();
            stage.setTitle(bundle.getString("title"));
            stage.setScene(new Scene(root, 520, 620));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the localized application view.", exception);
        }
    }

    Locale getLocale() {
        return locale;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
