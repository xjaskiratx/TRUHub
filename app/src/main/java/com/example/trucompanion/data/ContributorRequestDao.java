package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trucompanion.model.ContributorRequest;

import java.util.List;

@Dao
public interface ContributorRequestDao {

    @Insert
    long insertRequest(ContributorRequest request);

    @Update
    void updateRequest(ContributorRequest request);

    @Query("SELECT * FROM contributor_requests WHERE userId = :userId LIMIT 1")
    ContributorRequest getRequestByUser(int userId);
    @Query("SELECT * FROM contributor_requests WHERE status = 'pending'")
    List<ContributorRequest> getAllPendingRequests();

    @Query("SELECT * FROM contributor_requests WHERE clubId = :clubId AND status = 'pending'")
    List<ContributorRequest> getPendingRequestsForClub(int clubId);

    @Query("SELECT * FROM contributor_requests " +
            "WHERE userId = :userId AND clubId = :clubId " +
            "AND (status = 'pending' OR status = 'approved') " +
            "LIMIT 1")
    ContributorRequest getUserPendingOrApprovedRequest(int userId, int clubId);

    @Query("SELECT * FROM contributor_requests WHERE clubId = :clubId")
    List<ContributorRequest> getRequestsByClub(int clubId);
}
