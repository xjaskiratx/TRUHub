package com.example.trucompanion.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendCodeBtn;

    private AppDatabase db;

    public static String tempEmail;
    public static int tempOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        emailInput = findViewById(R.id.forgotEmailInput);
        sendCodeBtn = findViewById(R.id.btnSendCode);

        db = AppDatabase.getInstance(this);

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = db.userDao().getUserByEmail(email);
                if (user == null) {
                    Toast.makeText(ForgotPasswordActivity.this, "No account found with this email", Toast.LENGTH_SHORT).show();
                    return;
                }

                Random random = new Random();
                int otp = 1000 + random.nextInt(9000);

                tempEmail = email;
                tempOtp = otp;

                Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                startActivity(intent);
            }
        });
    }
}
