package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clubs")
public class Club {

    @PrimaryKey(autoGenerate = true)
    public int clubId;

    public String clubName;
    public String clubCategory;
    public String clubDescription;

    public int leaderId;
    public Integer coLeaderId;
    public Integer presidentId;

    public int memberCount;
    public long createdAt;
}
