package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trucompanion.model.Club;

import java.util.List;

@Dao
public interface ClubDao {

    @Insert
    long insertClub(Club club);

    @Update
    void updateClub(Club club);

    @Query("SELECT * FROM clubs")
    List<Club> getAllClubs();

    @Query("SELECT * FROM clubs WHERE clubId = :id LIMIT 1")
    Club getClubById(int id);

    @Query("SELECT * FROM clubs WHERE leaderId = :userId OR coLeaderId = :userId OR presidentId = :userId")
    List<Club> getClubsManagedByUser(int userId);

    @Query("DELETE FROM clubs WHERE clubId = :id")
    void deleteClub(int id);

    @Query("UPDATE clubs SET memberCount = :count WHERE clubId = :clubId")
    void updateMemberCount(int clubId, int count);

    @Query("SELECT memberCount FROM clubs WHERE clubId = :clubId LIMIT 1")
    int getMemberCount(int clubId);

    @Query("SELECT * FROM clubs WHERE clubId = :clubId LIMIT 1")
    Club refreshClub(int clubId);
}
