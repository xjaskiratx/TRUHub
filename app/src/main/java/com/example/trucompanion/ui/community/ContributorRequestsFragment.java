package com.example.trucompanion.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.ContributorRequest;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;

import java.util.ArrayList;
import java.util.List;

public class ContributorRequestsFragment extends Fragment {

    public ContributorRequestsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contributor_requests, container, false);

        AppDatabase db = AppDatabase.getDatabase(getContext());

        // -------------------------------
        // SAFETY: ensure session exists
        // -------------------------------
        Session session = db.sessionDao().getLastSession();
        if (session == null) return view;  // guest should never reach here

        User currentUser = db.userDao().getUserById(session.userId);
        if (currentUser == null) return view;

        List<Club> managedClubs = db.clubDao().getClubsManagedByUser(currentUser.uid);
        if (managedClubs.size() == 0) return view;  // no clubs to manage

        List<ContributorRequest> pendingAll = new ArrayList<>();

        for (Club club : managedClubs) {
            List<ContributorRequest> clubRequests =
                    db.contributorRequestDao().getPendingRequestsForClub(club.clubId);
            pendingAll.addAll(clubRequests);
        }

        RecyclerView recycler = view.findViewById(R.id.requestsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ContributorRequestsAdapter adapter =
                new ContributorRequestsAdapter(pendingAll, currentUser, db);

        recycler.setAdapter(adapter);

        return view;
    }
}
