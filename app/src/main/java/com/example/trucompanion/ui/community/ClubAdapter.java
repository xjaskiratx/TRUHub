package com.example.trucompanion.ui.community;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.User;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private final List<Club> clubList;
    private final User currentUser;
    private final OnClubDeleteRequestListener deleteListener;

    public interface OnClubDeleteRequestListener {
        void onDeleteClubRequested(Club club);
    }

    public ClubAdapter(List<Club> clubList, User currentUser, OnClubDeleteRequestListener deleteListener) {
        this.clubList = clubList;
        this.currentUser = currentUser;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubList.get(position);

        AppDatabase db = AppDatabase.getDatabase(holder.itemView.getContext());

        int freshCount = db.clubMemberDao().getMembersOfClub(club.clubId).size();
        club.memberCount = freshCount;
        db.clubDao().updateMemberCount(club.clubId, freshCount);

        User leader = db.userDao().getUserById(club.leaderId);
        String leaderName = (leader != null && leader.name != null && !leader.name.trim().isEmpty())
                ? leader.name
                : "Leader";

        holder.name.setText(club.clubName + " (" + leaderName + ")");
        holder.category.setText(club.clubCategory);
        holder.description.setText(club.clubDescription);
        holder.memberCount.setText("Members: " + freshCount);

        boolean isLeader = (currentUser != null && currentUser.uid == club.leaderId);
        holder.deleteIcon.setVisibility(isLeader ? View.VISIBLE : View.GONE);

        holder.deleteIcon.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClubRequested(club);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClubDetailsActivity.class);
            intent.putExtra("clubId", club.clubId);
            intent.putExtra("clubName", club.clubName);
            intent.putExtra("clubCategory", club.clubCategory);
            intent.putExtra("clubDescription", club.clubDescription);
            intent.putExtra("leaderId", club.leaderId);
            intent.putExtra("memberCount", freshCount); // ⭐ IMPORTANT: send LIVE count
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    static class ClubViewHolder extends RecyclerView.ViewHolder {

        TextView name, category, description, memberCount;
        ImageView deleteIcon;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemClubName);
            category = itemView.findViewById(R.id.itemClubCategory);
            description = itemView.findViewById(R.id.itemClubDescription);
            memberCount = itemView.findViewById(R.id.itemClubMembers);
            deleteIcon = itemView.findViewById(R.id.itemClubDelete);
        }
    }
}
