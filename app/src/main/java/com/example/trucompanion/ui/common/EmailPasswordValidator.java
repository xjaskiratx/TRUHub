package com.example.trucompanion.ui.common;

import android.util.Patterns;

public class EmailPasswordValidator {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        email = email.trim();

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        password = password.trim();

        return password.length() >= 6 && !password.contains(" ");
    }
}
