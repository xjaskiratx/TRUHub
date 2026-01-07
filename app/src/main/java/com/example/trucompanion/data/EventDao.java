package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trucompanion.model.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    long insertEvent(Event event);

    @Query("SELECT * FROM events ORDER BY eventId DESC")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE clubId = :clubId ORDER BY eventId DESC")
    List<Event> getEventsByClub(int clubId);

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    Event getEventById(int eventId);

    @Query("DELETE FROM events WHERE clubId = :id")
    void deleteEventsOfClub(int id);

    @Query("DELETE FROM events WHERE eventId = :id")
    void deleteEventById(int id);
}
