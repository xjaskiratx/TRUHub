package com.example.trucompanion.ui.community;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.ClubMember;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Arrays;
import java.util.List;

public class CreateClubActivity extends AppCompatActivity {

    private EditText clubNameInput, clubDescriptionInput;
    private Spinner categorySpinner;
    private Button createClubBtn;

    private AppDatabase db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = AppDatabase.getDatabase(this);

        Session session = db.sessionDao().getLastSession();
        if (session == null) {
            Toast.makeText(this, "You must be logged in to create a club.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser = db.userDao().getUserById(session.userId);

        if (currentUser.clubId != 0) {
            Club existingClub = db.clubDao().getClubById(currentUser.clubId);

            if (existingClub == null) {
                currentUser.clubId = 0;
                db.userDao().updateClubId(currentUser.uid, 0);
            }
        }

        initViews();
        setupCategorySpinner();

        createClubBtn.setOnClickListener(v -> createClub());
    }

    private void initViews() {
        clubNameInput = findViewById(R.id.clubNameInput);
        clubDescriptionInput = findViewById(R.id.clubDescriptionInput);
        categorySpinner = findViewById(R.id.clubCategorySpinner);
        createClubBtn = findViewById(R.id.createClubBtn);
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "Academic",
                "Cultural",
                "Sports",
                "Technology",
                "Arts",
                "Social",
                "Other"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        categorySpinner.setAdapter(adapter);
    }

    private void createClub() {

        if (currentUser.clubId != 0) {
            Club checkClub = db.clubDao().getClubById(currentUser.clubId);
            if (checkClub != null) {
                Toast.makeText(this, "You already created or joined a club.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String name = clubNameInput.getText().toString().trim();
        String desc = clubDescriptionInput.getText().toString().trim();
        String category = (String) categorySpinner.getSelectedItem();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a club name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (desc.isEmpty()) {
            Toast.makeText(this, "Enter a club description.", Toast.LENGTH_SHORT).show();
            return;
        }

        Club club = new Club();
        club.clubName = name;
        club.clubDescription = desc;
        club.clubCategory = category;
        club.leaderId = currentUser.uid;
        club.coLeaderId = null;
        club.presidentId = null;

        club.memberCount = 1;
        club.createdAt = System.currentTimeMillis();

        long clubId = db.clubDao().insertClub(club);

        if (clubId <= 0) {
            Toast.makeText(this, "Error creating club.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClubMember leaderEntry = new ClubMember();
        leaderEntry.userId = currentUser.uid;
        leaderEntry.clubId = (int) clubId;
        leaderEntry.joinedAt = System.currentTimeMillis();
        db.clubMemberDao().insertMember(leaderEntry);

        db.clubDao().updateMemberCount((int) clubId, 1);

        db.userDao().updateClubId(currentUser.uid, (int) clubId);

        db.userDao().updatePersona(currentUser.uid, "contributor");

        Toast.makeText(this, "Club created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
