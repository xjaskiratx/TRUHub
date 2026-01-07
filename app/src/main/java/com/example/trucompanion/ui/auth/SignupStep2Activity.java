package com.example.trucompanion.ui.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trucompanion.MainActivity;
import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.utils.ProfileImageUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SignupStep2Activity extends AppCompatActivity {

    private ImageView profilePreview;
    private Button btnTakePhoto, btnChooseGallery, btnSkip, btnSetPicture;

    private String name, email, password;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> takePhotoLauncher;

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        retrieveSignupData();
        initializeViews();
        setupImagePickers();
        setupButtons();

        if (selectedImageUri == null) {
            showInitialsPreview();
        }
    }

    private void retrieveSignupData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
    }

    private void initializeViews() {
        profilePreview = findViewById(R.id.profilePreviewCircle);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChooseGallery = findViewById(R.id.btnChooseGallery);
        btnSkip = findViewById(R.id.btnSkip);
        btnSetPicture = findViewById(R.id.btnSetPicture);
    }

    private void setupImagePickers() {

        takePhotoLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                // Camera returns a small "data" thumbnail as a Bitmap
                                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                                if (bitmap != null) {
                                    // ProfileImageUtil.saveBitmapAndGetUri should return a safe file:// URI
                                    selectedImageUri = ProfileImageUtil.saveBitmapAndGetUri(this, bitmap);

                                    // FIX: Convert to circle for preview
                                    Bitmap circularBitmap = ProfileImageUtil.getCircularBitmap(bitmap);
                                    profilePreview.setImageBitmap(circularBitmap);
                                }
                            }
                        });

        galleryLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                selectedImageUri = uri;

                                // FIX 1: Grant persistent read permission for the Content URI
                                getContentResolver().takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                                try {
                                    // The preview works because of the temporary permission and persistent grant
                                    InputStream inputStream = getContentResolver().openInputStream(uri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                    if (inputStream != null) inputStream.close();

                                    // FIX: Convert to circle for preview
                                    Bitmap circularBitmap = ProfileImageUtil.getCircularBitmap(bitmap);
                                    profilePreview.setImageBitmap(circularBitmap);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    private void setupButtons() {

        btnTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoLauncher.launch(intent);
        });

        btnChooseGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupStep3Activity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        });

        btnSetPicture.setOnClickListener(v -> finalizeSignupWithPhoto());
    }

    private void showInitialsPreview() {
        Bitmap initials = ProfileImageUtil.generateInitialsBitmap(this, name, "#3F51B5");
        profilePreview.setImageBitmap(initials);
    }

    private void finalizeSignupWithPhoto() {

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please choose a photo first.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);

        User user = new User();
        user.name = name;
        user.email = email.toLowerCase();
        user.password = password;
        user.persona = "student";

        user.profileColor = null;

        long userId = db.userDao().insertUser(user);

        String finalImageUriString = null;

        if (selectedImageUri.getScheme() != null && selectedImageUri.getScheme().equalsIgnoreCase("content")) {
            finalImageUriString = saveImageToInternalStorage(selectedImageUri, (int) userId);
        } else {
            finalImageUriString = selectedImageUri.toString();
        }

        if (finalImageUriString != null) {
            db.userDao().updateProfileImageUri((int) userId, finalImageUriString);
        }

        Session s = new Session();
        s.userId = (int) userId;
        db.sessionDao().insertSession(s);

        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
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
}