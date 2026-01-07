package com.example.trucompanion.ui.community;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.example.trucompanion.model.ClubMember;
import com.example.trucompanion.model.Event;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.ui.common.LoginRequiredFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EventsFragment extends Fragment implements EventAdapter.OnEventDeleteRequestListener {

    private AppDatabase db;
    private User currentUser;
    private RecyclerView recycler;
    private TextView emptyMsg;
    private EventAdapter adapter;
    private List<Event> events;
    private int currentUsersClubId = -1;

    public EventsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        db = AppDatabase.getDatabase(requireContext());
        Session session = db.sessionDao().getLastSession();

        if (session == null) {
            return LoginRequiredFragment
                    .newInstance("You need to log in to view or create events.")
                    .onCreateView(inflater, container, savedInstanceState);
        }

        currentUser = db.userDao().getUserById(session.userId);

        ClubMember membership = db.clubMemberDao().getMembershipForUser(currentUser.uid);
        if (membership != null) {
            currentUsersClubId = membership.clubId;
        }

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recycler = view.findViewById(R.id.eventsRecycler);
        emptyMsg = view.findViewById(R.id.emptyEventsMessage);
        FloatingActionButton fab = view.findViewById(R.id.fabAddEvent);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadEvents();

        if (currentUser.persona.equals("student")) {
            fab.setVisibility(View.GONE);
        }

        if (currentUser.persona.equals("contributor")) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v ->
                    startActivity(new Intent(getContext(), CreateEventActivity.class))
            );
        }

        return view;
    }

    private void loadEvents() {
        events = db.eventDao().getAllEvents();

        adapter = new EventAdapter(events, currentUser, currentUsersClubId, this);
        recycler.setAdapter(adapter);

        updateEmptyState();
    }

    private void updateEmptyState() {
        if (events.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyMsg.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteEvent(Event event) {

        // Safety check: must belong to this club
        if (event.clubId != currentUsersClubId) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> performDelete(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDelete(Event event) {
        db.eventDao().deleteEventById(event.eventId);
        loadEvents();  // refresh list
    }
}
