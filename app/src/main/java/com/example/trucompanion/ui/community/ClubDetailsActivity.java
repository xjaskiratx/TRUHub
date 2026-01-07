package com.example.trucompanion.ui.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.ClubMember;
import com.example.trucompanion.model.ContributorRequest;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.google.android.material.appbar.MaterialToolbar;

public class ClubDetailsActivity extends AppCompatActivity {

    TextView clubNameView, clubCategoryView, clubDescView, clubLeaderView, clubMembersView;
    Button applyRoleBtn;

    AppDatabase db;
    Club club;
    User leader;
    User currentUser;

    int clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_details);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = AppDatabase.getDatabase(this);

        clubId = getIntent().getIntExtra("clubId", -1);
        if (clubId == -1) {
            finish();
            return;
        }

        club = db.clubDao().getClubById(clubId);
        if (club == null) {
            finish();
            return;
        }

        leader = db.userDao().getUserById(club.leaderId);

        Session session = db.sessionDao().getLastSession();
        if (session != null) {
            currentUser = db.userDao().getUserById(session.userId);
        }

        initViews();

        clubNameView.setText(club.clubName);
        clubCategoryView.setText(club.clubCategory);
        clubDescView.setText(club.clubDescription);

        clubLeaderView.setText("Club Leader: " + (leader != null ? leader.name : "Leader"));
        clubMembersView.setText("Members: " + club.memberCount);

        evaluateButtonState();
    }

    private void initViews() {
        clubNameView = findViewById(R.id.clubDetailsName);
        clubCategoryView = findViewById(R.id.clubDetailsCategory);
        clubDescView = findViewById(R.id.clubDetailsDesc);
        clubLeaderView = findViewById(R.id.clubDetailsLeader);
        clubMembersView = findViewById(R.id.clubDetailsMembers);
        applyRoleBtn = findViewById(R.id.clubDetailsApplyBtn);
    }

    private void evaluateButtonState() {

        if (currentUser == null) {
            applyRoleBtn.setEnabled(false);
            applyRoleBtn.setText("Login Required");
            return;
        }

        int uid = currentUser.uid;

        if (club.leaderId == uid) {
            disableButton("You are the leader");
            return;
        }

        if (club.coLeaderId != null && club.coLeaderId == uid) {
            disableButton("You are already a co-leader");
            return;
        }

        if (club.presidentId != null && club.presidentId == uid) {
            disableButton("You are already a vice president");
            return;
        }

        ClubMember membership = db.clubMemberDao().getMembershipForUser(uid);
        if (membership != null && membership.clubId == clubId) {
            disableButton("You are already a member");
            return;
        }

        ContributorRequest existing =
                db.contributorRequestDao().getUserPendingOrApprovedRequest(uid, clubId);

        if (existing != null) {
            disableButton("Request already submitted");
            return;
        }

        applyRoleBtn.setEnabled(true);
        applyRoleBtn.setText("Apply for Role");

        applyRoleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ClubDetailsActivity.this, ApplyForRoleActivity.class);
            intent.putExtra("clubId", clubId);
            intent.putExtra("clubName", club.clubName);
            intent.putExtra("clubCategory", club.clubCategory);
            startActivity(intent);
        });
    }

    private void disableButton(String reason) {
        applyRoleBtn.setEnabled(false);
        applyRoleBtn.setText(reason);
    }
}
