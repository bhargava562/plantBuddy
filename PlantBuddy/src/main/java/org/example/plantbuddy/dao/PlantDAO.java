package org.example.plantbuddy.dao;

import org.example.plantbuddy.model.Plant;
import org.example.plantbuddy.util.DatabaseUtil;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.*;

public class PlantDAO {

    /**
     * Adds a new plant to the database
     * @param plant The plant object to be added
     * @return The database ID of the newly added plant
     * @throws SQLException If there's a database error
     */
    public int addPlant(Plant plant) throws SQLException {
        String sql = "INSERT INTO plants (name, species, location, light_needs, acquisition_date, " +
                "watering_frequency, fertilizing_frequency, image_path, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getSpecies());
            stmt.setString(3, plant.getLocation());
            stmt.setString(4, plant.getLightneeds());
            stmt.setDate(5, plant.getDateAcquired() != null ?
                    Date.valueOf(plant.getDateAcquired()) : null);
            stmt.setInt(6, plant.getWateringFrequency());
            stmt.setInt(7, plant.getFertilizingFrequency());
            stmt.setString(8, plant.getImagepath());
            stmt.setString(9, plant.getNotes());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating plant failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    plant.setId(id);

                    // Record initial care events
                    addCareEvent(id, "Initial Setup", "Plant added to system");

                    // Create reminders
                    createWateringReminder(plant);
                    createFertilizingReminder(plant);

                    return id;
                } else {
                    throw new SQLException("Creating plant failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Adds a care event for a plant
     * @param plantId The ID of the plant
     * @param eventType The type of event
     * @param notes Any notes about the event
     * @throws SQLException If there's a database error
     */
    public void addCareEvent(int plantId, String eventType, String notes) throws SQLException {
        String sql = "INSERT INTO care_events (plant_id, event_type, event_date, notes) " +
                "VALUES (?, ?, NOW(), ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plantId);
            stmt.setString(2, eventType);
            stmt.setString(3, notes);

            stmt.executeUpdate();
        }
    }

    /**
     * Creates a watering reminder for a plant
     * @param plant The plant to create the reminder for
     * @throws SQLException If there's a database error
     */
    public void createWateringReminder(Plant plant) throws SQLException {
        createReminder(plant.getId(), "Watering",
                LocalDate.now().plusDays(plant.getWateringFrequency()));
    }

    /**
     * Creates a fertilizing reminder for a plant
     * @param plant The plant to create the reminder for
     * @throws SQLException If there's a database error
     */
    public void createFertilizingReminder(Plant plant) throws SQLException {
        createReminder(plant.getId(), "Fertilizing",
                LocalDate.now().plusDays(plant.getFertilizingFrequency()));
    }

    /**
     * Creates a reminder entry in the database
     * @param plantId The ID of the plant
     * @param careType The type of care reminder
     * @param dueDate The due date for the care task
     * @throws SQLException If there's a database error
     */
    private void createReminder(int plantId, String careType, LocalDate dueDate) throws SQLException {
        String sql = "INSERT INTO reminders (plant_id, care_type, due_date, is_completed) " +
                "VALUES (?, ?, ?, false)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plantId);
            stmt.setString(2, careType);
            stmt.setDate(3, Date.valueOf(dueDate));

            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all plants from the database
     * @return List of all plants
     * @throws SQLException If there's a database error
     */
    public List<Plant> getAllPlants() throws SQLException {
        List<Plant> plants = new ArrayList<>();
        String sql = "SELECT * FROM plants";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Plant plant = mapResultSetToPlant(rs);
                plants.add(plant);
            }
        }

        return plants;
    }

    /**
     * Retrieves a plant by its ID
     * @param id The ID of the plant
     * @return The plant, or null if not found
     * @throws SQLException If there's a database error
     */
    public Plant getPlantById(int id) throws SQLException {
        String sql = "SELECT * FROM plants WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlant(rs);
                }
            }
        }

        return null;
    }

    /**
     * Updates an existing plant in the database
     * @param plant The plant to update
     * @throws SQLException If there's a database error
     */
    public void updatePlant(Plant plant) throws SQLException {
        String sql = "UPDATE plants SET name = ?, species = ?, location = ?, " +
                "light_needs = ?, acquisition_date = ?, watering_frequency = ?, " +
                "fertilizing_frequency = ?, image_path = ?, notes = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getSpecies());
            stmt.setString(3, plant.getLocation());
            stmt.setString(4, plant.getLightneeds());
            stmt.setDate(5, plant.getDateAcquired() != null ?
                    Date.valueOf(plant.getDateAcquired()) : null);
            stmt.setInt(6, plant.getWateringFrequency());
            stmt.setInt(7, plant.getFertilizingFrequency());
            stmt.setString(8, plant.getImagepath());
            stmt.setString(9, plant.getNotes());
            stmt.setInt(10, plant.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Records a watering event for a plant and updates the last watered date
     * @param plantId The ID of the plant
     * @throws SQLException If there's a database error
     */
    public void waterPlant(int plantId) throws SQLException {
        LocalDate today = LocalDate.now();
        addCareEvent(plantId, "Watering", "Plant watered on " + today);
        updateLastWatered(plantId, today);

        // Create a new reminder based on the plant's watering frequency
        Plant plant = getPlantById(plantId);
        if (plant != null) {
            // Remove existing watering reminders
            removeReminders(plantId, "Watering");
            // Create new watering reminder
            createWateringReminder(plant);
        }
    }

    /**
     * Records a fertilizing event for a plant and updates the last fertilized date
     * @param plantId The ID of the plant
     * @throws SQLException If there's a database error
     */
    public void fertilizePlant(int plantId) throws SQLException {
        LocalDate today = LocalDate.now();
        addCareEvent(plantId, "Fertilizing", "Plant fertilized on " + today);
        updateLastFertilized(plantId, today);

        // Create a new reminder based on the plant's fertilizing frequency
        Plant plant = getPlantById(plantId);
        if (plant != null) {
            // Remove existing fertilizing reminders
            removeReminders(plantId, "Fertilizing");
            // Create new fertilizing reminder
            createFertilizingReminder(plant);
        }
    }

    /**
     * Removes reminders of a specific type for a plant
     * @param plantId The ID of the plant
     * @param careType The type of care reminder
     * @throws SQLException If there's a database error
     */
    private void removeReminders(int plantId, String careType) throws SQLException {
        String sql = "DELETE FROM reminders WHERE plant_id = ? AND care_type = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plantId);
            stmt.setString(2, careType);

            stmt.executeUpdate();
        }
    }

    /**
     * Updates the last watered date for a plant
     * @param plantId The ID of the plant
     * @param date The date when the plant was watered
     * @throws SQLException If there's a database error
     */
    public void updateLastWatered(int plantId, LocalDate date) throws SQLException {
        // For a complete implementation, we would store these dates in the database
        // For now, this is a placeholder
        // The actual dates are stored as care events in the care_events table
    }

    /**
     * Updates the last fertilized date for a plant
     * @param plantId The ID of the plant
     * @param date The date when the plant was fertilized
     * @throws SQLException If there's a database error
     */
    public void updateLastFertilized(int plantId, LocalDate date) throws SQLException {
        // For a complete implementation, we would store these dates in the database
        // For now, this is a placeholder
        // The actual dates are stored as care events in the care_events table
    }

    /**
     * Deletes a plant from the database
     * @param id The ID of the plant to delete
     * @throws SQLException If there's a database error
     */
    public void deletePlant(int id) throws SQLException {
        // We don't need to manually delete care_events and reminders because
        // the ON DELETE CASCADE constraint in the foreign keys will handle that
        String sql = "DELETE FROM plants WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }

    /**
     * Gets all reminders for a specific plant
     * @param plantId The ID of the plant
     * @return List of all reminders for the plant
     * @throws SQLException If there's a database error
     */
    public List<Map<String, Object>> getRemindersForPlant(int plantId) throws SQLException {
        List<Map<String, Object>> reminders = new ArrayList<>();
        String sql = "SELECT * FROM reminders WHERE plant_id = ? AND is_completed = false ORDER BY due_date";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plantId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> reminder = new HashMap<>();
                    reminder.put("id", rs.getInt("id"));
                    reminder.put("plantId", rs.getInt("plant_id"));
                    reminder.put("careType", rs.getString("care_type"));
                    reminder.put("dueDate", rs.getDate("due_date").toLocalDate());
                    reminder.put("completed", rs.getBoolean("is_completed"));
                    reminders.add(reminder);
                }
            }
        }

        return reminders;
    }

    /**
     * Maps a ResultSet row to a Plant object
     * @param rs The ResultSet containing plant data
     * @return A populated Plant object
     * @throws SQLException If there's a database error
     */
    private Plant mapResultSetToPlant(ResultSet rs) throws SQLException {
        Plant plant = new Plant();
        plant.setId(rs.getInt("id"));
        plant.setName(rs.getString("name"));
        plant.setSpecies(rs.getString("species"));
        plant.setLocation(rs.getString("location"));
        plant.setLightneeds(rs.getString("light_needs"));

        Date acquisitionDate = rs.getDate("acquisition_date");
        if (acquisitionDate != null) {
            plant.setDateAcquired(acquisitionDate.toLocalDate());
        }

        plant.setWateringFrequency(rs.getInt("watering_frequency"));
        plant.setFertilizingFrequency(rs.getInt("fertilizing_frequency"));
        plant.setImagepath(rs.getString("image_path"));
        plant.setNotes(rs.getString("notes"));

        // Set last watered and fertilized dates based on care events
        setPlantCareHistory(plant);

        return plant;
    }

    /**
     * Sets the last watered and fertilized dates for a plant based on care events
     * @param plant The plant to update
     * @throws SQLException If there's a database error
     */
    private void setPlantCareHistory(Plant plant) throws SQLException {
        // Get the most recent watering event
        String waterSql = "SELECT MAX(event_date) as last_date FROM care_events " +
                "WHERE plant_id = ? AND event_type = 'Watering'";

        // Get the most recent fertilizing event
        String fertilizeSql = "SELECT MAX(event_date) as last_date FROM care_events " +
                "WHERE plant_id = ? AND event_type = 'Fertilizing'";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Process watering
            try (PreparedStatement stmt = conn.prepareStatement(waterSql)) {
                stmt.setInt(1, plant.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getTimestamp("last_date") != null) {
                        plant.setLastWatered(rs.getTimestamp("last_date").toLocalDateTime().toLocalDate());
                    } else {
                        plant.setLastWatered(plant.getDateAcquired());
                    }
                }
            }

            // Process fertilizing
            try (PreparedStatement stmt = conn.prepareStatement(fertilizeSql)) {
                stmt.setInt(1, plant.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getTimestamp("last_date") != null) {
                        plant.setLastFertilized(rs.getTimestamp("last_date").toLocalDateTime().toLocalDate());
                    } else {
                        plant.setLastFertilized(plant.getDateAcquired());
                    }
                }
            }
        }
    }
}