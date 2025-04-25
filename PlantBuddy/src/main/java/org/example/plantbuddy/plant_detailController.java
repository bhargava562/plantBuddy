package org.example.plantbuddy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.plantbuddy.dao.PlantDAO;
import org.example.plantbuddy.model.Plant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class plant_detailController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private PlantDAO plantDAO;
    private int plantId;
    private Plant currentPlant;

    // FXML elements for displaying plant details
    @FXML private Label plantNameLabel;
    @FXML private ImageView plantImageView;
    @FXML private Label speciesLabel;
    @FXML private Label locationLabel;
    @FXML private Label lightNeedsLabel;
    @FXML private Label acquiredLabel;
    @FXML private Label wateringLabel;
    @FXML private Label fertilizingLabel;
    @FXML private ProgressBar wateringProgressBar;
    @FXML private ProgressBar fertilizingProgressBar;
    @FXML private Label wateringDaysLabel;
    @FXML private Label fertilizingDaysLabel;
    @FXML private Button waterbtn;
    @FXML private Button fertilizebtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the DAO
        plantDAO = new PlantDAO();

        // Set up event handlers for care buttons
        waterbtn.setOnAction(event -> waterPlant());
        fertilizebtn.setOnAction(event -> fertilizePlant());
    }

    /**
     * Set the plant ID and load the plant details
     * @param plantId The ID of the plant to display
     */
    public void setPlantId(int plantId) {
        this.plantId = plantId;
        loadPlantDetails();
    }

    /**
     * Loads the plant details from the database and displays them
     */
    private void loadPlantDetails() {
        try {
            // Get plant details from database
            currentPlant = plantDAO.getPlantById(plantId);

            if (currentPlant == null) {
                System.err.println("Plant not found with ID: " + plantId);
                return;
            }

            // Set plant name in the header
            plantNameLabel.setText(currentPlant.getName());

            // Set plant image
            if (currentPlant.getImagepath() != null && !currentPlant.getImagepath().isEmpty()) {
                try {
                    File imageFile = new File(currentPlant.getImagepath());
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        plantImageView.setImage(image);
                    } else {
                        setDefaultImage();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading plant image: " + e.getMessage());
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }

            // Set other plant details
            speciesLabel.setText(currentPlant.getSpecies() != null ? currentPlant.getSpecies() : "Not specified");
            locationLabel.setText(currentPlant.getLocation());
            lightNeedsLabel.setText(currentPlant.getLightneeds());

            // Format acquisition date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            acquiredLabel.setText(currentPlant.getDateAcquired().format(formatter));

            // Set watering and fertilizing frequencies
            wateringLabel.setText("Every " + currentPlant.getWateringFrequency() + " days");
            fertilizingLabel.setText("Every " + currentPlant.getFertilizingFrequency() + " days");

            // Set progress bars for watering and fertilizing
            updateCareStatus();

        } catch (SQLException e) {
            System.err.println("Error loading plant details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the care status displays (progress bars and day counters)
     */
    private void updateCareStatus() {
        // Calculate days until next watering and fertilizing
        int daysUntilWatering = currentPlant.getDaysUntilWatering();
        int daysUntilFertilizing = currentPlant.getDaysUntilFertilizing();

        // Calculate progress percentages (inverse - less days means more progress)
        double wateringProgress = 1.0 - (double) daysUntilWatering / currentPlant.getWateringFrequency();
        double fertilizingProgress = 1.0 - (double) daysUntilFertilizing / currentPlant.getFertilizingFrequency();

        // Ensure progress values are between 0 and 1
        wateringProgress = Math.max(0, Math.min(1, wateringProgress));
        fertilizingProgress = Math.max(0, Math.min(1, fertilizingProgress));

        // Update progress bars
        wateringProgressBar.setProgress(wateringProgress);
        fertilizingProgressBar.setProgress(fertilizingProgress);

        // Update day counters
        wateringDaysLabel.setText(daysUntilWatering + " days");
        fertilizingDaysLabel.setText(daysUntilFertilizing + " days");
    }

    /**
     * Sets a default image when no plant image is available
     */
    private void setDefaultImage() {
        try {
            // Try to load a placeholder image
            Image placeholderImage = new Image(getClass().getResourceAsStream("/placeholder_plant.png"));
            if (placeholderImage != null && !placeholderImage.isError()) {
                plantImageView.setImage(placeholderImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
        }
    }

    /**
     * Records a watering event for the current plant
     */
    private void waterPlant() {
        try {
            plantDAO.waterPlant(plantId);
            // Reload plant details to update care status
            loadPlantDetails();
        } catch (SQLException e) {
            System.err.println("Error watering plant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Records a fertilizing event for the current plant
     */
    private void fertilizePlant() {
        try {
            plantDAO.fertilizePlant(plantId);
            // Reload plant details to update care status
            loadPlantDetails();
        } catch (SQLException e) {
            System.err.println("Error fertilizing plant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            root = loader.load();

            // Get the controller and refresh the plant list
            HelloController controller = loader.getController();
            controller.refreshPlants();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            String css = this.getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}