package com.example.trucompanion.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.google.android.material.appbar.MaterialToolbar;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextView otpDisplay;
    private EditText otpInput;
    private Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        otpDisplay = findViewById(R.id.otpDisplay);
        otpInput = findViewById(R.id.otpInput);
        verifyBtn = findViewById(R.id.btnVerifyOtp);

        otpDisplay.setText("Your verification code is: " + ForgotPasswordActivity.tempOtp);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredOtpStr = otpInput.getText().toString().trim();

                if (enteredOtpStr.isEmpty()) {
                    Toast.makeText(OtpVerificationActivity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                int enteredOtp;

                try {
                    enteredOtp = Integer.parseInt(enteredOtpStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(OtpVerificationActivity.this, "Invalid OTP format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (enteredOtp == ForgotPasswordActivity.tempOtp) {
                    Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
