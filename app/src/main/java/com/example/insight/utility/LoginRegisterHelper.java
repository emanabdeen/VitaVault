package com.example.insight.utility;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterHelper {
    private boolean passwordChangeSuccessful = false;
    private boolean oldPasswordCorrect = false;

    public static boolean validPassword(String userPW) {
        //password must have 8 chars, including one uppercase, one lowercase, one number,
        // and one special character
        //for example: Firebase1$
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return userPW.matches(passwordPattern);
    }

    public boolean validateOldPasswordCorrect(String oldPW, FirebaseUser user) {
        oldPasswordCorrect = false;
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPW);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                oldPasswordCorrect = true;
            }
        });
        return oldPasswordCorrect;
    }
    public boolean validateNewPasswordMatchesConfirmPassword(String newPw, String confirmPw){
        boolean newPwMatchesConfirmPw;
        if (newPw.equals(confirmPw)) {
            newPwMatchesConfirmPw = true;
        } else {
            newPwMatchesConfirmPw = false;
        }
        return newPwMatchesConfirmPw;
    }

    public String changePasword(String oldPW, String newPw, String confirmPw, FirebaseUser user) {
        passwordChangeSuccessful = false; // make sure status is false to start process
        if (!validateNewPasswordMatchesConfirmPassword(newPw, confirmPw)) { // confirm new password and confirm password match before anything else
            return "New password and confirm password do not match.";
        }
        if (!validateOldPasswordCorrect(oldPW, user)) { // confirm old password is correct, and re-authenticate to allow the password change call to work
            return "Old password does not match current password.";
        }
        try {
            user.updatePassword(newPw).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e("tag", "changePassword:success");
                    passwordChangeSuccessful = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("tag", "changePassword:failure - " + e.getMessage());
                    passwordChangeSuccessful = false;
                }
            });
        } catch (Exception e) {
            Log.e("account-mgmt", "Error during password change: " + e.getMessage());
        }
        if(!passwordChangeSuccessful) {
            return "Password change failed, please contact the development team for support.";
        }
        return "Password changed successfully";
    }

}
