package com.example.trucompanion.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.model.Event;
import com.example.trucompanion.model.User;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final User currentUser;
    private final int currentUsersClubId;
    private final OnEventDeleteRequestListener deleteListener;

    public interface OnEventDeleteRequestListener {
        void onDeleteEvent(Event event);
    }

    public EventAdapter(List<Event> events,
                        User currentUser,
                        int currentUsersClubId,
                        OnEventDeleteRequestListener deleteListener) {
        this.eventList = events;
        this.currentUser = currentUser;
        this.currentUsersClubId = currentUsersClubId;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event e = eventList.get(position);

        holder.title.setText(e.title);
        holder.description.setText(e.description);
        holder.dateTime.setText(e.date + " • " + e.time);

        boolean isMemberOfThisClub = (currentUser != null && currentUsersClubId == e.clubId);

        holder.deleteIcon.setVisibility(isMemberOfThisClub ? View.VISIBLE : View.GONE);

        holder.deleteIcon.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteEvent(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, dateTime;
        ImageView deleteIcon;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            dateTime = itemView.findViewById(R.id.eventDateTime);

            deleteIcon = itemView.findViewById(R.id.eventDeleteIcon);
        }
    }
}
