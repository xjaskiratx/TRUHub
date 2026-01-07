package com.example.trucompanion.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trucompanion.model.Task;
import com.example.trucompanion.model.User;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.Event;
import com.example.trucompanion.model.ContributorRequest;
import com.example.trucompanion.model.ClubMember;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Task.class,
                User.class,
                Session.class,
                Club.class,
                Event.class,
                ContributorRequest.class,
                ClubMember.class
        },
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public abstract ChecklistDao checklistDao();
    public abstract UserDao userDao();
    public abstract SessionDao sessionDao();
    public abstract ClubDao clubDao();
    public abstract EventDao eventDao();
    public abstract ContributorRequestDao contributorRequestDao();
    public abstract ClubMemberDao clubMemberDao();

    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "trucompanion_db"
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static synchronized AppDatabase getInstance(Context context) {
        return getDatabase(context);
    }
}