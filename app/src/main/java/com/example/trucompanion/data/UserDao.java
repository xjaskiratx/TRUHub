package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.trucompanion.model.User;

@Dao
public interface UserDao {

    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserById(int uid);

    @Query("UPDATE users SET name = :name, email = :email, profileColor = :profileColor, profileImageUri = :profileImageUri WHERE uid = :userId")
    void updateProfileWithImage(
            int userId,
            String name,
            String email,
            String profileColor,
            String profileImageUri
    );

    @Query("UPDATE users SET profileImageUri = :uriString WHERE uid = :userId")
    void updateProfileImageUri(int userId, String uriString);

    @Query("UPDATE users SET persona = :persona WHERE uid = :userId")
    void updatePersona(int userId, String persona);

    @Query("UPDATE users SET clubId = :clubId WHERE uid = :userId")
    void updateClubId(int userId, int clubId);

    @Query("UPDATE users SET persona = 'student', clubId = 0 WHERE clubId = :clubId")
    void revertMembersToStudent(int clubId);

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    void updatePassword(String email, String newPassword);

    @Delete
    void deleteUser(User user);

    @Update
    void update(User user);
}
