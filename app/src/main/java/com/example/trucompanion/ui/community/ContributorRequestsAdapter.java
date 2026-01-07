package com.example.trucompanion.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Club;
import com.example.trucompanion.model.ClubMember;
import com.example.trucompanion.model.ContributorRequest;
import com.example.trucompanion.model.User;

import java.util.List;

public class ContributorRequestsAdapter extends RecyclerView.Adapter<ContributorRequestsAdapter.RequestViewHolder> {

    private final List<ContributorRequest> requests;
    private final User currentUser;
    private final AppDatabase db;

    public ContributorRequestsAdapter(List<ContributorRequest> requests,
                                      User currentUser,
                                      AppDatabase db) {
        this.requests = requests;
        this.currentUser = currentUser;
        this.db = db;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ContributorRequest req = requests.get(position);

        User applicant = db.userDao().getUserById(req.userId);
        Club club = db.clubDao().getClubById(req.clubId);

        String typeLabel = req.requestType.equals("leadership")
                ? "Leadership Request"
                : "Contributor Request";

        holder.title.setText(typeLabel + " - " + club.clubName);
        holder.user.setText("Applicant: " + applicant.name);
        holder.role.setText("Requested Role: " + req.roleRequested);
        holder.description.setText("Description: " + req.description);

        holder.approveBtn.setOnClickListener(v ->
                handleApproval(req, applicant, club, true, position));

        holder.declineBtn.setOnClickListener(v ->
                handleApproval(req, applicant, club, false, position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void handleApproval(ContributorRequest r, User applicant, Club club,
                                boolean approved, int pos) {

        if (!approved) {
            r.status = "declined";
            db.contributorRequestDao().updateRequest(r);
            requests.remove(pos);
            notifyItemRemoved(pos);
            return;
        }

        if (r.requestType.equals("leadership")) {
            assignLeadershipRole(r, applicant, club);
        } else {
            promoteToContributorOnly(applicant);
        }

        addUserToClubMembership(applicant.uid, club);

        int count = db.clubMemberDao().getMembersOfClub(club.clubId).size();
        club.memberCount = count;
        db.clubDao().updateMemberCount(club.clubId, count); // safer direct update

        db.userDao().updateClubId(applicant.uid, club.clubId);

        r.status = "approved";
        db.contributorRequestDao().updateRequest(r);

        requests.remove(pos);
        notifyItemRemoved(pos);
    }

    private void assignLeadershipRole(ContributorRequest req, User applicant, Club club) {

        String role = req.roleRequested;

        switch (role) {
            case "co_leader":
                club.coLeaderId = applicant.uid;
                break;

            case "vice_president":
                club.presidentId = applicant.uid;
                break;

            case "events_manager":
            case "social_media_manager":
            case "treasurer":
            case "secretary":
            default:
                break;
        }

        db.clubDao().updateClub(club);

        db.userDao().updatePersona(applicant.uid, "contributor");
    }

    private void promoteToContributorOnly(User applicant) {
        db.userDao().updatePersona(applicant.uid, "contributor");
    }

    private void addUserToClubMembership(int userId, Club club) {

        boolean isAlreadyMember =
                db.clubMemberDao().isUserMemberOfClub(userId, club.clubId);

        if (isAlreadyMember) return;

        ClubMember member = new ClubMember();
        member.userId = userId;
        member.clubId = club.clubId;
        member.joinedAt = System.currentTimeMillis();

        db.clubMemberDao().insertMember(member);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView title, user, role, description;
        Button approveBtn, declineBtn;

        public RequestViewHolder(@NonNull View v) {
            super(v);

            title = v.findViewById(R.id.reqTitle);
            user = v.findViewById(R.id.reqUser);
            role = v.findViewById(R.id.reqRole);
            description = v.findViewById(R.id.reqDescription);
            approveBtn = v.findViewById(R.id.reqApproveBtn);
            declineBtn = v.findViewById(R.id.reqDeclineBtn);
        }
    }
}
