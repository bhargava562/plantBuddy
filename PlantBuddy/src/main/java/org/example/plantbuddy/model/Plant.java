package org.example.plantbuddy.model;

import java.time.LocalDate;

public class Plant {
    private int id;
    private String name;
    private String species;
    private String location;
    private String lightneeds;
    private LocalDate dateAcquired;
    private int wateringFrequency;
    private int fertilizingFrequency;
    private String notes;
    private String imagepath;
    private LocalDate lastWatered;
    private LocalDate lastFertilized;

    public Plant() {
        this.dateAcquired = LocalDate.now();
        this.wateringFrequency = 7;
        this.fertilizingFrequency = 30;
        this.lastWatered = LocalDate.now();
        this.lastFertilized = LocalDate.now();
    }
    public Plant(int id,String name,String species,String location,String lightneeds,LocalDate dateAcquired,int wateringFrequency,int fertilizingFrequency,String notes,String imagepath) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.location = location;
        this.lightneeds = lightneeds;
        this.dateAcquired = dateAcquired;
        this.wateringFrequency = wateringFrequency;
        this.fertilizingFrequency = fertilizingFrequency;
        this.notes = notes;
        this.imagepath = imagepath;
        this.lastWatered = LocalDate.now();
        this.lastFertilized = LocalDate.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSpecies() {
        return species;
    }
    public void setSpecies(String species) {
        this.species = species;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getLightneeds() {
        return lightneeds;
    }
    public void setLightneeds(String lightneeds) {
        this.lightneeds = lightneeds;
    }
    public LocalDate getDateAcquired() {
        return dateAcquired;
    }
    public void setDateAcquired(LocalDate dateAcquired) {
        this.dateAcquired = dateAcquired;
    }
    public int getWateringFrequency() {
        return wateringFrequency;
    }
    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }
    public int getFertilizingFrequency() {
        return fertilizingFrequency;
    }
    public void setFertilizingFrequency(int fertilizingFrequency) {
        this.fertilizingFrequency = fertilizingFrequency;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getImagepath() {
        return imagepath;
    }
    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
    public LocalDate getLastWatered() {
        return lastWatered;
    }
    public void setLastWatered(LocalDate lastWatered) {
        this.lastWatered = lastWatered;
    }
    public LocalDate getLastFertilized() {
        return lastFertilized;
    }
    public void setLastFertilized(LocalDate lastFertilized) {
        this.lastFertilized = lastFertilized;
    }
    public int getDaysUntilWatering() {
        return wateringFrequency - (int)java.time.temporal.ChronoUnit.DAYS.between(lastWatered, LocalDate.now());
    }
    public int getDaysUntilFertilizing() {
        return fertilizingFrequency - (int)java.time.temporal.ChronoUnit.DAYS.between(lastFertilized, LocalDate.now());
    }

    @Override
    public String toString() {
        return name+" ("+species+")";
    }
}
