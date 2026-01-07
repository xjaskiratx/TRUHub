package com.example.trucompanion.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trucompanion.R;
import com.example.trucompanion.ui.auth.LoginActivity;
import com.example.trucompanion.ui.auth.SignupActivity;

public class LoginRequiredFragment extends Fragment {

    private static final String ARG_MESSAGE = "message";

    public static LoginRequiredFragment newInstance(String message) {
        LoginRequiredFragment fragment = new LoginRequiredFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_required, container, false);

        TextView tvMessage = view.findViewById(R.id.tvMessage);
        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        Button btnLoginInstead = view.findViewById(R.id.btnLoginInstead);

        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "";
        tvMessage.setText(message);

        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SignupActivity.class)));

        btnLoginInstead.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), LoginActivity.class)));

        return view;
    }
}
