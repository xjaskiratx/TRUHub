package com.example.trucompanion.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.utils.ProfileImageUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private final String[] PROFILE_COLORS = {
            "#2563EB",
            "#059669",
            "#D97706",
            "#FACC15",
            "#DC2626"
    };

    private Uri selectedImageUri = null;

    private View[] colorViews;
    private String selectedColor;
    private String savedImagePath = null;

    private EditText nameInput, emailInput;
    private Button saveBtn;
    private ImageView profilePreview;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Session session = db.sessionDao().getLastSession();
        User user = db.userDao().getUserById(session.userId);

        nameInput = findViewById(R.id.editNameInput);
        emailInput = findViewById(R.id.editEmailInput);
        saveBtn = findViewById(R.id.saveProfileBtn);
        profilePreview = findViewById(R.id.profileEditImageCircle);

        nameInput.setText(user.name);
        emailInput.setText(user.email);

        selectedColor = (user.profileColor != null) ? user.profileColor : PROFILE_COLORS[0];
        savedImagePath = user.profileImageUri;

        loadExistingImageOrInitials(savedImagePath, user.name, selectedColor);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri uri = result.getData().getData();
                        if (uri == null) return;

                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        selectedImageUri = uri;

                        savedImagePath = null;

                        loadExistingImageOrInitials(uri.toString(), user.name, selectedColor);
                    }
                }
        );

        profilePreview.setOnClickListener(v -> openDocumentPicker());

        setupColorPickers();

        saveBtn.setOnClickListener(v -> {

            String finalImagePath;

            if (selectedImageUri != null) {
                finalImagePath = saveImageToInternalStorage(selectedImageUri, user.uid);
            } else if (savedImagePath != null) {
                finalImagePath = savedImagePath;
            } else {
                finalImagePath = null;
            }

            db.userDao().updateProfileWithImage(
                    user.uid,
                    nameInput.getText().toString().trim(),
                    emailInput.getText().toString().trim().toLowerCase(),
                    selectedColor,
                    finalImagePath
            );

            finish();
        });

    }

    private void loadExistingImageOrInitials(String uriString, String name, String color) {
        if (uriString != null) {
            try {
                Uri uri = Uri.parse(uriString);
                Bitmap bitmap = null;

                if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("file")) {
                    bitmap = BitmapFactory.decodeFile(uri.getPath());
                } else if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("content")) {
                    InputStream is = getContentResolver().openInputStream(uri);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    }
                }

                if (bitmap != null) {
                    Bitmap circularBitmap = ProfileImageUtil.getCircularBitmap(bitmap);
                    profilePreview.setImageBitmap(circularBitmap);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setInitialsFallback(name, color);
    }

    private String saveImageToInternalStorage(Uri uri, int userId) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File file = new File(getFilesDir(), "profile_user_" + userId + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file).toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void setupColorPickers() {
        View c1 = findViewById(R.id.colorEdit1);
        View c2 = findViewById(R.id.colorEdit2);
        View c3 = findViewById(R.id.colorEdit3);
        View c4 = findViewById(R.id.colorEdit4);
        View c5 = findViewById(R.id.colorEdit5);

        colorViews = new View[]{c1, c2, c3, c4, c5};

        for (int i = 0; i < colorViews.length; i++) {
            GradientDrawable bg = (GradientDrawable) colorViews[i].getBackground().mutate();
            bg.setColor(Color.parseColor(PROFILE_COLORS[i]));
            int index = i;

            colorViews[i].setOnClickListener(v -> {
                if (savedImagePath != null || selectedImageUri != null) {
                    showColorSelectionWarning(index);
                } else {
                    applyColorSelection(index);
                }
            });
        }

        updateSelection(findSelectedIndex(selectedColor));
    }

    private void showColorSelectionWarning(int colorIndex) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Profile Change")
                .setMessage("Choosing a color will permanently remove your current profile photo. Are you sure you want to proceed?")
                .setPositiveButton("Yes, Remove Photo", (dialog, which) -> {
                    applyColorSelection(colorIndex);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void applyColorSelection(int index) {
        if (savedImagePath != null) {
            try {
                Uri oldUri = Uri.parse(savedImagePath);
                if (oldUri.getScheme() != null && oldUri.getScheme().equalsIgnoreCase("file")) {
                    File oldFile = new File(oldUri.getPath());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectedColor = PROFILE_COLORS[index];

        savedImagePath = null;
        selectedImageUri = null;

        setInitialsFallback(nameInput.getText().toString(), selectedColor);
        updateSelection(index);
    }

    private void setInitialsFallback(String name, String color) {
        Bitmap initialsBitmap = ProfileImageUtil.generateInitialsBitmap(this, name, color);
        profilePreview.setImageBitmap(initialsBitmap);
    }

    private int findSelectedIndex(String color) {
        for (int i = 0; i < PROFILE_COLORS.length; i++) {
            if (PROFILE_COLORS[i].equalsIgnoreCase(color)) return i;
        }
        return 0;
    }

    private void updateSelection(int selectedIndex) {
        for (int i = 0; i < colorViews.length; i++) {
            GradientDrawable bg = (GradientDrawable) colorViews[i].getBackground().mutate();
            bg.setStroke(4, (i == selectedIndex ? Color.BLACK : Color.TRANSPARENT));
        }
    }
}