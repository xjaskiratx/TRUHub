package com.example.trucompanion.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.User;
import com.example.trucompanion.ui.common.EmailPasswordValidator;
import com.google.android.material.appbar.MaterialToolbar;

public class SignupActivity extends AppCompatActivity {

    EditText inputName, inputEmail, inputPassword;
    TextView signupEmailError, signupPasswordError;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnNext = findViewById(R.id.btnSignup);   // reused ID
        signupEmailError = findViewById(R.id.signupEmailError);
        signupPasswordError = findViewById(R.id.signupPasswordError);

        AppDatabase db = AppDatabase.getDatabase(this);

        inputEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = inputEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    signupEmailError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidEmail(email)) {
                    signupEmailError.setVisibility(View.VISIBLE);
                    signupEmailError.setText("• Please enter a valid email address");
                } else {
                    signupEmailError.setVisibility(View.GONE);
                }
            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();

                if (password.isEmpty()) {
                    signupPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(password)) {
                    signupPasswordError.setVisibility(View.VISIBLE);
                    signupPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    signupPasswordError.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        inputPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = inputPassword.getText().toString().trim();

                if (password.isEmpty()) {
                    signupPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(password)) {
                    signupPasswordError.setVisibility(View.VISIBLE);
                    signupPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    signupPasswordError.setVisibility(View.GONE);
                }
            }
        });

        btnNext.setText("Next");

        btnNext.setOnClickListener(v -> {

            String name = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim().toLowerCase();
            String password = inputPassword.getText().toString().trim();

            // BASIC VALIDATION
            if (name.isEmpty()) {
                inputName.setError("Name is required");
                return;
            }

            if (!EmailPasswordValidator.isValidEmail(email)) {
                signupEmailError.setVisibility(View.VISIBLE);
                signupEmailError.setText("• Please enter a valid email address");
                return;
            }

            if (!EmailPasswordValidator.isValidPassword(password)) {
                signupPasswordError.setVisibility(View.VISIBLE);
                signupPasswordError.setText("• Minimum 6 characters\n• No spaces");
                return;
            }

            User exists = db.userDao().getUserByEmail(email);
            if (exists != null) {
                Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, SignupStep2Activity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        });
    }
}
