package com.example.trucompanion.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.ui.auth.LoginActivity;
import com.example.trucompanion.ui.auth.SignupActivity;
import com.example.trucompanion.ui.common.LoginRequiredFragment;
import com.google.android.material.chip.Chip;

public class CommunityFragment extends Fragment {

    private Chip chipEvents, chipClubs;

    public CommunityFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        Session session = db.sessionDao().getLastSession();

        if (session == null) {
            View guestView = inflater.inflate(R.layout.fragment_login_required, container, false);

            TextView btnCreateAccount = guestView.findViewById(R.id.btnSignUp);
            TextView btnLogin = guestView.findViewById(R.id.btnLoginInstead);

            btnCreateAccount.setOnClickListener(v ->
                    startActivity(new Intent(getContext(), SignupActivity.class)));

            btnLogin.setOnClickListener(v ->
                    startActivity(new Intent(getContext(), LoginActivity.class)));

            return guestView;
        }

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        chipEvents = view.findViewById(R.id.chipEvents);
        chipClubs = view.findViewById(R.id.chipClubs);

        loadChildFragment(new EventsFragment());
        chipEvents.setChecked(true);

        chipEvents.setOnClickListener(v -> {
            loadChildFragment(new EventsFragment());
            chipEvents.setChecked(true);
            chipClubs.setChecked(false);
        });

        chipClubs.setOnClickListener(v -> {
            loadChildFragment(new ClubsFragment());
            chipClubs.setChecked(true);
            chipEvents.setChecked(false);
        });

        return view;
    }

    private void loadChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.communityContentContainer, fragment)
                .commit();
    }
}
