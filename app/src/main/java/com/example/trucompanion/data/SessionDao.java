package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trucompanion.model.Session;

@Dao
public interface SessionDao {

    @Insert
    long insertSession(Session session);

    @Query("SELECT * FROM sessions ORDER BY sessionId DESC LIMIT 1")
    Session getLastSession();

    @Query("DELETE FROM sessions")
    void deleteAll();

    @Query("DELETE FROM sessions WHERE userId = :userId")
    void deleteSession(int userId);
}