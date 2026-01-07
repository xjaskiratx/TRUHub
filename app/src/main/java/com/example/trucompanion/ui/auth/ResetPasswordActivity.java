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
import com.example.trucompanion.ui.common.EmailPasswordValidator;
import com.google.android.material.appbar.MaterialToolbar;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordInput, confirmPasswordInput;
    private TextView resetPasswordError, resetConfirmError;
    private Button resetPasswordBtn;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        resetPasswordError = findViewById(R.id.resetPasswordError);
        resetConfirmError = findViewById(R.id.resetConfirmError);
        resetPasswordBtn = findViewById(R.id.btnResetPassword);

        db = AppDatabase.getInstance(this);

        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString().trim();

                if (pass.isEmpty()) {
                    resetPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(pass)) {
                    resetPasswordError.setVisibility(View.VISIBLE);
                    resetPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    resetPasswordError.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        newPasswordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pass = newPasswordInput.getText().toString().trim();

                if (pass.isEmpty()) {
                    resetPasswordError.setVisibility(View.GONE);
                    return;
                }

                if (!EmailPasswordValidator.isValidPassword(pass)) {
                    resetPasswordError.setVisibility(View.VISIBLE);
                    resetPasswordError.setText("• Minimum 6 characters\n• No spaces");
                } else {
                    resetPasswordError.setVisibility(View.GONE);
                }
            }
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = newPasswordInput.getText().toString().trim();
                String confirm = s.toString().trim();

                if (confirm.isEmpty()) {
                    resetConfirmError.setVisibility(View.GONE);
                    return;
                }

                if (!confirm.equals(pass)) {
                    resetConfirmError.setVisibility(View.VISIBLE);
                    resetConfirmError.setText("• Passwords do not match");
                } else {
                    resetConfirmError.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        confirmPasswordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pass = newPasswordInput.getText().toString().trim();
                String confirm = confirmPasswordInput.getText().toString().trim();

                if (confirm.isEmpty()) {
                    resetConfirmError.setVisibility(View.GONE);
                    return;
                }

                if (!confirm.equals(pass)) {
                    resetConfirmError.setVisibility(View.VISIBLE);
                    resetConfirmError.setText("• Passwords do not match");
                } else {
                    resetConfirmError.setVisibility(View.GONE);
                }
            }
        });

        resetPasswordBtn.setOnClickListener(v -> {

            String newPass = newPasswordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();

            if (!EmailPasswordValidator.isValidPassword(newPass)) {
                resetPasswordError.setVisibility(View.VISIBLE);
                resetPasswordError.setText("• Minimum 6 characters\n• No spaces");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                resetConfirmError.setVisibility(View.VISIBLE);
                resetConfirmError.setText("• Passwords do not match");
                return;
            }

            String email = ForgotPasswordActivity.tempEmail;
            db.userDao().updatePassword(email, newPass);

            Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
