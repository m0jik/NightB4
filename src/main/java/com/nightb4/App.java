package com.nightb4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main JavaFX application entry point.
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Load the “primary” (today) FXML by default.
        Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
        // Create a Scene at a fixed size of 950×950:
        Scene scene = new Scene(root, 950, 950);

        stage.setScene(scene);
        stage.setTitle("NightB4");

        // Set up a global “on-close” hook:
        //   Before JavaFX actually closes the window, force-save both Today and Tomorrow data.
        stage.setOnCloseRequest(evt -> {
            // Save today's lists:
            PrimaryController.persistAllSections();
            // Save tomorrow's lists:
            SecondaryController.persistTomorrowSections();
            // Now JavaFX will continue shutting down.
        });

        stage.show();
    }

    /**
     * Utility for switching scenes.
     * Call App.setRoot("secondary") to load secondary.fxml, etc.
     * Preserves the same 950×950 size by reusing the existing Scene.
     */
    public static void setRoot(String fxmlName) throws Exception {
        Parent pane = FXMLLoader.load(App.class.getResource(fxmlName + ".fxml"));
        primaryStage.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
