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

import com.example.trucompanion.MainActivity;
import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.example.trucompanion.ui.common.EmailPasswordValidator;
import com.google.android.material.appbar.MaterialToolbar;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    TextView loginEmailError, loginPasswordError;
    TextView forgotPasswordText;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getDatabase(this);

        Session session = db.sessionDao().getLastSession();

        if (session != null) {
            Intent intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        loginEmailError = findViewById(R.id.loginEmailError);
        loginPasswordError = findViewById(R.id.loginPasswordError);

        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        loginEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = loginEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    loginEmailError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidEmail(email)) {
                    loginEmailError.setVisibility(View.VISIBLE);
                    loginEmailError.setText("• Please enter a valid email address");
                } else {
                    loginEmailError.setVisibility(View.GONE);
                }
            }
        });

        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { // <-- FIX APPLIED HERE
                String password = s.toString().trim();

                if (password.isEmpty()) {
                    loginPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(password)) {
                    loginPasswordError.setVisibility(View.VISIBLE);
                    loginPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    loginPasswordError.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        loginPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = loginPassword.getText().toString().trim();

                if (password.isEmpty()) {
                    loginPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(password)) {
                    loginPasswordError.setVisibility(View.VISIBLE);
                    loginPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    loginPasswordError.setVisibility(View.GONE);
                }
            }
        });

        btnLogin.setOnClickListener(v -> {

            String email = loginEmail.getText().toString().trim().toLowerCase();
            String password = loginPassword.getText().toString().trim();

            if (!EmailPasswordValidator.isValidEmail(email)) {
                loginEmailError.setVisibility(View.VISIBLE);
                loginEmailError.setText("• Please enter a valid email address");
                return;
            }

            if (!EmailPasswordValidator.isValidPassword(password)) {
                loginPasswordError.setVisibility(View.VISIBLE);
                loginPasswordError.setText("• Minimum 6 characters\n• No spaces");
                return;
            }

            User u = db.userDao().getUserByEmail(email);

            if (u == null || !u.password.equals(password)) {
                Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                return;
            }

            Session s = new Session();
            s.userId = u.uid;
            db.sessionDao().insertSession(s);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}