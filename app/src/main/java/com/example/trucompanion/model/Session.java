package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class Session {

    @PrimaryKey(autoGenerate = true)
    public int sessionId;

    public int userId;

    public long loginTimestamp = System.currentTimeMillis();
}
