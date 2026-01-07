package com.example.trucompanion.ui.community;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.ClubMember;
import com.example.trucompanion.model.ContributorRequest;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ApplyForRoleActivity extends AppCompatActivity {

    private Spinner clubSpinner, roleSpinner;
    private EditText customRoleInput, roleDescriptionInput;
    private Button applyRoleBtn;

    private List<Club> clubs = new ArrayList<>();
    private AppDatabase db;

    private static final String ROLE_CO_LEADER = "co_leader";
    private static final String ROLE_VP = "vice_president";
    private static final String ROLE_EVENTS = "events_manager";
    private static final String ROLE_SMM = "social_media_manager";
    private static final String ROLE_TREASURER = "treasurer";
    private static final String ROLE_SECRETARY = "secretary";
    private static final String ROLE_CONTRIBUTOR = "general_contributor";
    private static final String ROLE_CUSTOM = "custom";

    private int incomingClubId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_role);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        clubSpinner = findViewById(R.id.clubSpinner);
        roleSpinner = findViewById(R.id.roleSpinner);
        customRoleInput = findViewById(R.id.customRoleInput);
        roleDescriptionInput = findViewById(R.id.roleDescriptionInput);
        applyRoleBtn = findViewById(R.id.applyRoleBtn);

        db = AppDatabase.getDatabase(this);

        Session session = db.sessionDao().getLastSession();
        if (session == null) {
            Toast.makeText(this, "Login required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User currentUser = db.userDao().getUserById(session.userId);

        incomingClubId = getIntent().getIntExtra("clubId", -1);

        if (incomingClubId != -1) {
            Club openedClub = db.clubDao().getClubById(incomingClubId);

            if (openedClub != null) {

                // Block leader
                if (openedClub.leaderId == currentUser.uid) {
                    Toast.makeText(this, "You are the leader of this club.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // Block co-leader
                if (openedClub.coLeaderId != null && openedClub.coLeaderId == currentUser.uid) {
                    Toast.makeText(this, "You are already a co-leader.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                // Block vice-president
                if (openedClub.presidentId != null && openedClub.presidentId == currentUser.uid) {
                    Toast.makeText(this, "You are already a vice president.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }

            ClubMember m = db.clubMemberDao().getMembershipForUser(currentUser.uid);
            if (m != null && m.clubId == incomingClubId) {
                Toast.makeText(this, "You are already a member of this club.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            ContributorRequest existingReq =
                    db.contributorRequestDao().getUserPendingOrApprovedRequest(
                            currentUser.uid,
                            incomingClubId
                    );

            if (existingReq != null) {
                Toast.makeText(this, "You already have a request for this club.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        clubs = db.clubDao().getAllClubs();
        if (clubs.size() == 0) {
            Toast.makeText(this, "No clubs available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<String> clubNames = new ArrayList<>();
        for (Club c : clubs) clubNames.add(c.clubName);

        ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                clubNames
        );
        clubSpinner.setAdapter(clubAdapter);

        if (incomingClubId != -1) {
            for (int i = 0; i < clubs.size(); i++) {
                if (clubs.get(i).clubId == incomingClubId) {
                    clubSpinner.setSelection(i);
                    clubSpinner.setEnabled(false);
                    break;
                }
            }
        }

        String[] roles = new String[]{
                "Co-Leader",
                "Vice President",
                "Events Manager",
                "Social Media Manager",
                "Treasurer",
                "Secretary",
                "Contributor (General Member)",
                "Other (Custom Role)"
        };

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = roles[position];
                customRoleInput.setVisibility(
                        "Other (Custom Role)".equals(selected) ? View.VISIBLE : View.GONE
                );
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        applyRoleBtn.setOnClickListener(v -> {

            int clubIndex = clubSpinner.getSelectedItemPosition();
            Club selectedClub = clubs.get(clubIndex);

            String description = roleDescriptionInput.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(this, "Please describe your role.", Toast.LENGTH_SHORT).show();
                return;
            }

            String roleDisplay = (String) roleSpinner.getSelectedItem();
            String roleKey;
            String requestType;

            switch (roleDisplay) {
                case "Co-Leader": roleKey = ROLE_CO_LEADER; requestType = "leadership"; break;
                case "Vice President": roleKey = ROLE_VP; requestType = "leadership"; break;
                case "Events Manager": roleKey = ROLE_EVENTS; requestType = "leadership"; break;
                case "Social Media Manager": roleKey = ROLE_SMM; requestType = "leadership"; break;
                case "Treasurer": roleKey = ROLE_TREASURER; requestType = "leadership"; break;
                case "Secretary": roleKey = ROLE_SECRETARY; requestType = "leadership"; break;
                case "Contributor (General Member)": roleKey = ROLE_CONTRIBUTOR; requestType = "contributor"; break;
                case "Other (Custom Role)":
                    String custom = customRoleInput.getText().toString().trim();
                    if (custom.isEmpty()) {
                        Toast.makeText(this, "Enter custom role.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    roleKey = ROLE_CUSTOM + ":" + custom;
                    requestType = "leadership";
                    break;
                default:
                    Toast.makeText(this, "Invalid role.", Toast.LENGTH_SHORT).show();
                    return;
            }

            ContributorRequest req = new ContributorRequest();
            req.userId = currentUser.uid;
            req.clubId = selectedClub.clubId;
            req.requestType = requestType;
            req.roleRequested = roleKey;
            req.description = description;
            req.status = "pending";
            req.timestamp = System.currentTimeMillis();

            db.contributorRequestDao().insertRequest(req);

            Toast.makeText(this, "Request submitted.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
