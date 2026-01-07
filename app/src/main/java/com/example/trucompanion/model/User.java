package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String name;
    public String email;
    public String password;

    public String persona;

    public int clubId;

    public String profileColor;

    public String profileImageUri;
}
