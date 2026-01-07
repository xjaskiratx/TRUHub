package com.example.trucompanion.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.trucompanion.R;
import com.example.trucompanion.MainActivity; // <-- Required to call logoutUser()
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.ui.auth.LoginActivity;
import com.example.trucompanion.ui.community.ContributorRequestsFragment;
import com.example.trucompanion.ui.profile.EditProfileActivity;
import com.example.trucompanion.utils.ProfileImageUtil;

import java.io.File;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private AppDatabase db;
    private User currentUser;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        db = AppDatabase.getDatabase(getContext());
        Session session = db.sessionDao().getLastSession();

        if (session == null) {
            View guestView = inflater.inflate(R.layout.fragment_profile_guest, container, false);

            Button createBtn = guestView.findViewById(R.id.btnCreateAccount);
            Button loginBtn = guestView.findViewById(R.id.btnLogin);

            createBtn.setOnClickListener(v ->
                    startActivity(new Intent(getContext(),
                            com.example.trucompanion.ui.auth.SignupActivity.class)));

            loginBtn.setOnClickListener(v ->
                    startActivity(new Intent(getContext(),
                            com.example.trucompanion.ui.auth.LoginActivity.class)));

            return guestView;
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentUser = db.userDao().getUserById(session.userId);

        ImageView profileImage = view.findViewById(R.id.profileImageCircle);
        TextView fullNamePersonaView = view.findViewById(R.id.profileFullNamePersona);

        TextView actionEditProfile = view.findViewById(R.id.actionEditProfile);
        TextView actionLogout = view.findViewById(R.id.actionLogout);
        TextView actionBecomeContributor = view.findViewById(R.id.actionBecomeContributor);
        TextView actionApplyClubRole = view.findViewById(R.id.actionApplyClubRole);
        TextView actionClubRequests = view.findViewById(R.id.actionClubRequests);

        TextView actionDeleteAccount = view.findViewById(R.id.actionDeleteAccount);

        loadUserData(
                profileImage,
                fullNamePersonaView,
                actionBecomeContributor,
                actionApplyClubRole,
                actionClubRequests
        );

        actionEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class)));

        actionLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).logoutUser();
            }
        });

        if (actionDeleteAccount != null) {
            actionDeleteAccount.setOnClickListener(v -> showDeleteAccountWarning());
        }

        actionBecomeContributor.setOnClickListener(v ->
                startActivity(new Intent(getContext(),
                        com.example.trucompanion.ui.community.CreateClubActivity.class)));

        actionApplyClubRole.setOnClickListener(v ->
                startActivity(new Intent(getContext(),
                        com.example.trucompanion.ui.community.ApplyForRoleActivity.class)));

        actionClubRequests.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ContributorRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (db == null) return;

        Session session = db.sessionDao().getLastSession();

        if (session == null) {
            return;
        }

        currentUser = db.userDao().getUserById(session.userId);

        View view = getView();
        if (view == null) return;

        ImageView profileImage = view.findViewById(R.id.profileImageCircle);
        TextView fullNamePersonaView = view.findViewById(R.id.profileFullNamePersona);
        TextView actionBecomeContributor = view.findViewById(R.id.actionBecomeContributor);
        TextView actionApplyClubRole = view.findViewById(R.id.actionApplyClubRole);
        TextView actionClubRequests = view.findViewById(R.id.actionClubRequests);

        loadUserData(
                profileImage,
                fullNamePersonaView,
                actionBecomeContributor,
                actionApplyClubRole,
                actionClubRequests
        );
    }

    private void loadUserData(ImageView profileImage,
                              TextView fullNamePersonaView,
                              TextView actionBecomeContributor,
                              TextView actionApplyClubRole,
                              TextView actionClubRequests) {

        String fullName = currentUser.name;
        String persona = currentUser.persona;

        if (currentUser.profileImageUri != null && !currentUser.profileImageUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(currentUser.profileImageUri);
                Bitmap bitmap = null;

                if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("file")) {
                    bitmap = BitmapFactory.decodeFile(uri.getPath());
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        ImageDecoder.Source source =
                                ImageDecoder.createSource(requireContext().getContentResolver(), uri);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    } else {
                        InputStream is = requireContext().getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                }

                if (bitmap != null) {
                    Bitmap circularBitmap = ProfileImageUtil.getCircularBitmap(bitmap);
                    profileImage.setImageBitmap(circularBitmap);
                } else {
                    setInitialsFallback(profileImage, fullName, currentUser.profileColor);
                }

            } catch (Exception e) {
                e.printStackTrace();
                setInitialsFallback(profileImage, fullName, currentUser.profileColor);
            }
        } else {
            setInitialsFallback(profileImage, fullName, currentUser.profileColor);
        }

        fullNamePersonaView.setText(fullName + " – " + persona);

        if ("contributor".equals(persona)) {
            actionClubRequests.setVisibility(View.VISIBLE);
            actionBecomeContributor.setVisibility(View.GONE);
            actionApplyClubRole.setVisibility(View.GONE);
        } else {
            actionClubRequests.setVisibility(View.GONE);
            actionBecomeContributor.setVisibility(View.VISIBLE);
            actionApplyClubRole.setVisibility(View.VISIBLE);
        }
    }


    private void setInitialsFallback(ImageView profileImage, String fullName, String colorHex) {
        Bitmap bitmap = ProfileImageUtil.generateInitialsBitmap(
                requireContext(),
                fullName,
                colorHex != null ? colorHex : "#3F51B5"
        );
        profileImage.setImageBitmap(bitmap);
    }

    private void showDeleteAccountWarning() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Account Deletion")
                .setMessage("Are you absolutely sure you want to delete your account? This action is irreversible and all your data will be permanently removed.")
                .setPositiveButton("Yes, Delete Account", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteAccount() {
        // Crucial: Run database operations off the main thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int userId = currentUser.uid;

            // 1. Delete associated profile image file (Cleanup)
            if (currentUser.profileImageUri != null) {
                try {
                    Uri uri = Uri.parse(currentUser.profileImageUri);
                    if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("file")) {
                        File imageFile = new File(uri.getPath());
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            db.userDao().deleteUser(currentUser);

            db.sessionDao().deleteSession(userId);

            requireActivity().runOnUiThread(() -> {
                requireActivity().recreate();
            });
        });
    }
}