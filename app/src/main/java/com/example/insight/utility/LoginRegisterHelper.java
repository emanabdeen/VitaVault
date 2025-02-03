package com.example.insight.utility;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class LoginRegisterHelper {

    public static boolean validPassword(String userPW) {
        //password must have 8 chars, including one uppercase, one lowercase, one number,
        // and one special character
        //for example: Firebase1$
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return userPW.matches(passwordPattern);
    }

    public static void checkRegistrationMessage(Intent registerIntent, TextView title, ConstraintLayout successMessage){
        //Read Intent value. if registration process succeeded, will show success message
        boolean registerSuccess = registerIntent.getBooleanExtra("registerSuccess",false);
        // Set visibility based on registerSuccess
        if (registerSuccess) {
            title.setVisibility(View.GONE);
            successMessage.setVisibility(View.VISIBLE);
        } else {
            successMessage.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
        }
    }
}
