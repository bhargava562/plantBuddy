package org.example.plantbuddy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
       String css = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        Image icon = new Image("logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("Plant Buddy!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            // Consume the event to prevent immediate exit
            event.consume();

            // Show confirmation dialog
            if (showConfirmationDialog()) {
                stage.close();
            }
        });

        stage.show();
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        // Wait for user response
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public static void main(String[] args) {
        launch();
    }
}