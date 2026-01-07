package com.example.trucompanion.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trucompanion.model.Task;

import java.util.List;

@Dao
public interface ChecklistDao {

    @Query("SELECT * FROM tasks ORDER BY completed ASC, id DESC")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE isDefault = 1 ORDER BY completed ASC, id DESC")
    List<Task> getDefaultTasks();

    @Query("SELECT * FROM tasks WHERE isDefault = 0 ORDER BY completed ASC, id DESC")
    List<Task> getCustomTasks();

    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query ORDER BY completed ASC, id DESC")
    List<Task> searchTasks(String query);

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
