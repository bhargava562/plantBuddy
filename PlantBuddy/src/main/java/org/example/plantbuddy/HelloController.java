package org.example.plantbuddy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.plantbuddy.dao.PlantDAO;
import org.example.plantbuddy.model.Plant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private PlantDAO plantDAO;

    @FXML
    private TilePane plantTilePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the DAO
        plantDAO = new PlantDAO();

        // Set tile pane properties
        plantTilePane.setPrefColumns(3); // 3 columns
        plantTilePane.setHgap(15); // Horizontal gap
        plantTilePane.setVgap(15); // Vertical gap
        plantTilePane.setPadding(new Insets(20)); // Padding around the tile pane

        // Load plants from the database and display them
        loadPlants();
    }

    /**
     * Loads plants from the database and displays them in the UI
     */
    private void loadPlants() {
        try {
            // Clear any existing plants
            plantTilePane.getChildren().clear();

            // Get plants from database
            List<Plant> plants = plantDAO.getAllPlants();

            // Display plants in tile pane
            for (Plant plant : plants) {
                plantTilePane.getChildren().add(createPlantTile(plant));
            }

            System.out.println("Loaded " + plants.size() + " plants from database");

        } catch (SQLException e) {
            System.err.println("Error loading plants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a tile representing a plant
     * @param plant The plant to create a tile for
     * @return A StackPane containing the plant image and name
     */
    private StackPane createPlantTile(Plant plant) {
        // Create a stack pane as the container
        StackPane tileContainer = new StackPane();
        tileContainer.setPrefSize(150, 150);
        tileContainer.getStyleClass().add("plant-tile");

        // Set user data for the plant ID (used when tile is clicked)
        tileContainer.setUserData(plant.getId());

        // Create a VBox to hold image and label
        VBox content = new VBox();
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setSpacing(5);

        // Create image view
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        // Set plant image if available
        if (plant.getImagepath() != null && !plant.getImagepath().isEmpty()) {
            try {
                File imageFile = new File(plant.getImagepath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    setDefaultImage(imageView);
                }
            } catch (Exception e) {
                System.err.println("Error loading plant image: " + e.getMessage());
                setDefaultImage(imageView);
            }
        } else {
            setDefaultImage(imageView);
        }

        // Create label for plant name
        Label nameLabel = new Label(plant.getName());
        nameLabel.getStyleClass().add("plant-name");

        // Add image and label to VBox
        content.getChildren().addAll(imageView, nameLabel);

        // Add VBox to StackPane
        tileContainer.getChildren().add(content);

        // Add click event to navigate to plant detail view
        tileContainer.setOnMouseClicked(event -> openPlantDetail(event, plant.getId()));

        return tileContainer;
    }

    /**
     * Sets a default image for the plant tile when no image is available
     * @param imageView The ImageView to set the default image for
     */
    private void setDefaultImage(ImageView imageView) {
        try {
            // Try to load a placeholder image
            Image placeholderImage = new Image(getClass().getResourceAsStream("/placeholder_plant.png"));
            if (placeholderImage != null && !placeholderImage.isError()) {
                imageView.setImage(placeholderImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
        }
    }

    /**
     * Opens the plant detail view for the selected plant
     * @param event The mouse event
     * @param plantId The ID of the plant to display details for
     */
    private void openPlantDetail(MouseEvent event, int plantId) {
        try {
            // Load the plant_detail.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("plant_detail.fxml"));
            root = loader.load();

            // Get the controller and set the plant ID
            plant_detailController controller = loader.getController();
            controller.setPlantId(plantId);

            // Get the stage
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML
            scene = new Scene(root);

            // Add CSS to the scene
            String css = this.getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Set the scene on the stage and show it
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading plant_detail.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void addplant(ActionEvent event) throws IOException {
        try {
            // Load the add_plant.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_plant.fxml"));
            root = loader.load();

            // Get the stage from the event source
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML
            scene = new Scene(root);

            // Add CSS to the scene
            String css = this.getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Set the scene on the stage and show it
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading add_plant.fxml: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Refreshes the plant list, called after adding or updating a plant
     */
    public void refreshPlants() {
        loadPlants();
    }
}