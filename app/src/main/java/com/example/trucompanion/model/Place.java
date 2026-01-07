package com.example.trucompanion.model;

public class Place {

    private final String title;
    private final String description;
    private final String category;
    private final String subcategory;
    private final double latitude;
    private final double longitude;

    public Place(String title, String description, String category, String subcategory,
                 double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getSubcategory() { return subcategory; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
