package org.example.plantbuddy;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.example.plantbuddy.dao.PlantDAO;
import org.example.plantbuddy.model.Plant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class addplantController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ChoiceBox<String> mylocation;
    @FXML
    private ChoiceBox<String> mylightneed;
    @FXML
    private Label mywater;
    @FXML
    private Label myfertilizing;
    @FXML
    private Slider myw;
    @FXML
    private Slider myf;
    @FXML
    private Button chooseImageButton;
    @FXML
    private ImageView plantImageView;
    @FXML
    private TextField plantNameTextField;
    @FXML
    private TextField speciesTextField;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private DatePicker acquisitionDatePicker;

    @FXML
    private Button saveButton;

    private File selectedImageFile;  // To store the selected image file
    private PlantDAO plantDAO;

    int waterlevel, fertilizlevel;

    private String[] lightneeds = {"Light Direct", "Light Indirect", "Dark Direct", "Dark Indirect"};
    private String[] locations = {"Living Room", "Balcony", "Veranda", "Kitchen"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DAO
        plantDAO = new PlantDAO();

        // Initialize choice boxes
        mylocation.getItems().addAll(locations);
        mylightneed.getItems().addAll(lightneeds);

        // Set default values
        mylocation.setValue(locations[0]);
        mylightneed.setValue(lightneeds[0]);

        // Set acquisition date to today by default
        acquisitionDatePicker.setValue(LocalDate.now());

        // Configure watering slider
        myw.setMin(1);
        myw.setMax(30);
        myw.setValue(7); // Default value
        waterlevel = 7;
        mywater.setText(waterlevel + " days");

        // Configure fertilizing slider
        myf.setMin(7);
        myf.setMax(90);
        myf.setValue(30); // Default value
        fertilizlevel = 30;
        myfertilizing.setText(fertilizlevel + " days");

        // Add listener for watering slider
        myw.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                waterlevel = newValue.intValue();
                mywater.setText(waterlevel + " days");
            }
        });

        // Add listener for fertilizing slider
        myf.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                fertilizlevel = newValue.intValue();
                myfertilizing.setText(fertilizlevel + " days");
            }
        });

        // Set up image choosing functionality
        chooseImageButton.setOnAction(event -> handleImageSelection());

        // Set a placeholder image
        setPlaceholderImage();
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the image selection process when the "Choose Image" button is clicked
     */
    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Plant Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Show file chooser dialog
        selectedImageFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        if (selectedImageFile != null) {
            // Display the selected image
            Image image = new Image(selectedImageFile.toURI().toString());
            plantImageView.setImage(image);
            plantImageView.setFitWidth(155);
            plantImageView.setFitHeight(140);
            plantImageView.setPreserveRatio(true);
        }
    }

    /**
     * Sets a placeholder image when no plant image is selected
     */
    private void setPlaceholderImage() {
        try {
            // Try to load a placeholder image from resources
            Image placeholderImage = new Image(getClass().getResourceAsStream("/plant_placeholder.png"));
            if (placeholderImage != null && !placeholderImage.isError()) {
                plantImageView.setImage(placeholderImage);
                plantImageView.setFitWidth(155);
                plantImageView.setFitHeight(140);
                plantImageView.setPreserveRatio(true);
            }
        } catch (Exception e) {
            // If the placeholder image can't be loaded, leave the image view empty
            System.out.println("Could not load placeholder image: " + e.getMessage());
        }
    }

    /**
     * Helper method to show alerts
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Saves the selected image to the application's image directory
     * @return The path where the image is saved, or null if no image was selected
     */
    private String saveImageFile() {
        if (selectedImageFile == null) {
            return null;
        }

        try {
            // Create directory for images if it doesn't exist
            Path imageDir = Paths.get(System.getProperty("user.dir"), "plant_images");
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            // Generate a unique filename based on timestamp and original filename
            String originalFileName = selectedImageFile.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String newFileName = System.currentTimeMillis() + extension;

            // Copy the image file to our application's image directory
            Path targetPath = imageDir.resolve(newFileName);
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void savePlant(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Create plant object
            Plant plant = new Plant();
            plant.setName(plantNameTextField.getText().trim());
            plant.setSpecies(speciesTextField.getText().trim());
            plant.setLocation(mylocation.getValue());
            plant.setLightneeds(mylightneed.getValue());
            plant.setDateAcquired(acquisitionDatePicker.getValue());
            plant.setWateringFrequency(waterlevel);
            plant.setFertilizingFrequency(fertilizlevel);
            plant.setNotes(notesTextArea.getText().trim());

            // Save image if one was selected
            String imagePath = saveImageFile();
            plant.setImagepath(imagePath);

            // Save plant to database
            int plantId = plantDAO.addPlant(plant);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Plant added successfully with ID: " + plantId);

            // Return to main view
            back(event);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to save plant: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates the user input before saving the plant
     * @return true if all required fields are valid, false otherwise
     */
    private boolean validateInputs() {
        // Check if plant name is provided
        if (plantNameTextField.getText() == null || plantNameTextField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Plant name is required.");
            return false;
        }

        // Check if location is selected
        if (mylocation.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a location.");
            return false;
        }

        // Check if light needs is selected
        if (mylightneed.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select light needs.");
            return false;
        }

        // Check if acquisition date is provided
        if (acquisitionDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select an acquisition date.");
            return false;
        }

        return true;
    }
}