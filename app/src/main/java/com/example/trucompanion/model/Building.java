package com.example.trucompanion.model;

public class Building {

    private final String name;
    private final int imageResId;
    private final String description;

    public Building(String name, int imageResId, String description) {
        this.name = name;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}
