package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contributor_requests")
public class ContributorRequest {

    @PrimaryKey(autoGenerate = true)
    public int requestId;

    public int userId;
    public int clubId;

    public String requestType;

    public String roleRequested;

    public String description;

    public String status;

    public long timestamp;
}
