package com.example.trucompanion.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.trucompanion.MainActivity;
import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.utils.ProfileImageUtil;
import com.google.android.material.appbar.MaterialToolbar;

public class SignupStep3Activity extends AppCompatActivity {

    private ImageView previewCircle;
    private LinearLayout colorRow;
    private Button btnBack, btnDone;

    private String name, email, password;
    private String selectedColor = "#2563EB";

    private final String[] PROFILE_COLORS = {
            "#2563EB", // Vibrant Royal Blue
            "#059669", // Emerald Green
            "#D97706", // Rich Amber
            "#FACC15", // Yellow
            "#DC2626" // Deep Crimson Red
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step3);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        retrieveData();
        initializeViews();
        setupColorChoices();
        setupButtons();
        updateInitialsPreview();
    }

    private void retrieveData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
    }

    private void initializeViews() {
        previewCircle = findViewById(R.id.signup3PreviewCircle);
        colorRow = findViewById(R.id.signup3ColorPaletteRow);

        btnBack = findViewById(R.id.signup3BackBtn);
        btnDone = findViewById(R.id.signup3DoneBtn);
    }

    private void setupColorChoices() {
        for (String color : PROFILE_COLORS) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70);
            params.setMargins(16, 0, 16, 0);
            View circle = new View(this);
            circle.setLayoutParams(params);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(color));
            bg.setStroke(4, Color.TRANSPARENT);
            circle.setBackground(bg);

            circle.setOnClickListener(v -> {
                selectedColor = color;
                highlightSelectedColor(v);
                updateInitialsPreview();
            });

            colorRow.addView(circle);
        }
    }

    private void highlightSelectedColor(View selectedView) {
        for (int i = 0; i < colorRow.getChildCount(); i++) {
            View v = colorRow.getChildAt(i);
            GradientDrawable bg = (GradientDrawable) v.getBackground().mutate();
            if (v == selectedView) {
                bg.setStroke(6, Color.BLACK);
            } else {
                bg.setStroke(4, Color.TRANSPARENT);
            }
        }
    }

    private void updateInitialsPreview() {
        Bitmap bmp = ProfileImageUtil.generateInitialsBitmap(this, name, selectedColor);
        previewCircle.setImageBitmap(bmp);
    }

    private void setupButtons() {

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnDone.setOnClickListener(v -> finalizeSignup());
    }

    private void finalizeSignup() {

        AppDatabase db = AppDatabase.getInstance(this);

        User user = new User();
        user.name = name;
        user.email = email.toLowerCase();
        user.password = password;
        user.persona = "student";
        user.profileColor = selectedColor;
        user.profileImageUri = null;

        long userId = db.userDao().insertUser(user);

        Session s = new Session();
        s.userId = (int) userId;
        db.sessionDao().insertSession(s);

        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
