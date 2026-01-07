package com.example.trucompanion.ui.community;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.ui.common.LoginRequiredFragment;

import java.util.List;

public class ClubsFragment extends Fragment implements ClubAdapter.OnClubDeleteRequestListener {

    private AppDatabase db;
    private User currentUser;
    private RecyclerView recycler;
    private TextView emptyMsg;
    private ClubAdapter adapter;
    private List<Club> clubs;

    public ClubsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        db = AppDatabase.getDatabase(requireContext());
        Session session = db.sessionDao().getLastSession();

        if (session == null) {
            return LoginRequiredFragment
                    .newInstance("You need to log in to view or join clubs.")
                    .onCreateView(inflater, container, savedInstanceState);
        }

        currentUser = db.userDao().getUserById(session.userId);

        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        recycler = view.findViewById(R.id.clubsRecycler);
        emptyMsg = view.findViewById(R.id.emptyClubsMessage);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadClubs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClubs();
    }

    private void loadClubs() {
        clubs = db.clubDao().getAllClubs();

        adapter = new ClubAdapter(clubs, currentUser, this);
        recycler.setAdapter(adapter);

        updateEmptyState();
    }

    private void updateEmptyState() {
        if (clubs.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyMsg.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteClubRequested(Club club) {

        if (currentUser.uid != club.leaderId) {
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Club")
                .setMessage("Are you sure you want to delete this club?\nAll members will revert to Student and all events will be removed.")
                .setPositiveButton("Delete", (dialog, which) -> performClubDeletion(club))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performClubDeletion(Club club) {

        int clubId = club.clubId;

        db.userDao().revertMembersToStudent(clubId);

        db.clubMemberDao().deleteMembersOfClub(clubId);

        db.eventDao().deleteEventsOfClub(clubId);

        db.clubDao().deleteClub(clubId);

        loadClubs();
    }
}
