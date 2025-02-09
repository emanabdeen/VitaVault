package com.example.insight.utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LoginRegisterHelper {
    private static final String TAG = "LoginRegisterHelper";

    public static boolean validPassword(String userPW) {
        //password must have 8 chars, including one uppercase, one lowercase, one number,
        // and one special character
        //for example: Firebase1$
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return userPW.matches(passwordPattern);
    }

    public boolean validateOldPasswordCorrect(String oldPW, FirebaseAuth mAuth) throws InterruptedException {
        final boolean[] oldPasswordCorrectResult = {false};
        CountDownLatch latch = new CountDownLatch(1);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "validateOldPasswordCorrect:user is null");
            return false;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPW);

        if (credential == null) {
            Log.e(TAG, "validateOldPasswordCorrect:credential is null");
            return false;
        }
        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "reAuthenticate:success");
                oldPasswordCorrectResult[0] = true;
                latch.countDown();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "reAuthenticate:failure - " + e.getMessage());
                oldPasswordCorrectResult[0] = false;
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            Log.e(TAG, "validateOldPasswordCorrect:latch timed out");
            return false;
        }
        return oldPasswordCorrectResult[0];
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

    // Not final code for this method, just WIP for now with a way to get a status output
    public String changePassword(String oldPW, String newPw, String confirmPw, FirebaseAuth mAuth) throws InterruptedException {
        final boolean[] passwordChangeSuccessful = {false}; // make sure status is false to start process
        if (!validateNewPasswordMatchesConfirmPassword(newPw, confirmPw)) { // confirm new password and confirm password match before anything else
            return "New password and confirm password do not match.";
        }
        if (!validateOldPasswordCorrect(oldPW, mAuth)) { // confirm old password is correct, and re-authenticate to allow the password change call to work
            return "Old password does not match current password.";
        }
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            user.updatePassword(newPw).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e("tag", "changePassword:success");
                    passwordChangeSuccessful[0] = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("tag", "changePassword:failure - " + e.getMessage());
                    passwordChangeSuccessful[0] = false;
                }
            });
        } catch (Exception e) {
            Log.e("account-mgmt", "Error during password change: " + e.getMessage());
        }
        if(!passwordChangeSuccessful[0]) {
            return "Password change failed, please contact the development team for support.";
        }
        return "Password changed successfully";
    }

}
