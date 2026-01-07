package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.trucompanion.model.ClubMember;

import java.util.List;

@Dao
public interface ClubMemberDao {

    @Insert
    long insertMember(ClubMember member);

    @Query("SELECT * FROM club_members WHERE clubId = :clubId")
    List<ClubMember> getMembersOfClub(int clubId);

    @Query("SELECT * FROM club_members WHERE userId = :userId LIMIT 1")
    ClubMember getMembershipForUser(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM club_members WHERE userId = :userId AND clubId = :clubId)")
    boolean isUserMemberOfClub(int userId, int clubId);

    @Query("DELETE FROM club_members WHERE clubId = :id")
    void deleteMembersOfClub(int id);
}
