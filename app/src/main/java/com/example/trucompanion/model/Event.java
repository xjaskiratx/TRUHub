package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    public int eventId;

    public String title;
    public String description;
    public String date;
    public String time;

    public int clubId;
    public int contributorId;
}
