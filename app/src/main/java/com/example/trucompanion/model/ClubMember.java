package com.example.trucompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "club_members")
public class ClubMember {

    @PrimaryKey(autoGenerate = true)
    public int memberId;

    public int clubId;
    public int userId;
    public long joinedAt;
}
