package com.example.insight.utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginRegisterHelper {
    private static final String TAG = "LoginRegisterHelper";
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static boolean validPassword(String userPW) {
        //password must have 8 chars, including one uppercase, one lowercase, one number,
        // and one special character
        //for example: Firebase1$
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return userPW.matches(passwordPattern);
    }

    public CompletableFuture<Boolean> validateOldPasswordCorrect(String oldPW, FirebaseAuth mAuth) throws InterruptedException {
        //final boolean[] oldPasswordCorrectResult = {false};
        CompletableFuture<Boolean> futureBool = new CompletableFuture<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "validateOldPasswordCorrect:user is null");
            futureBool.complete(false);
            return futureBool;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPW);

        if (credential == null) {
            Log.e(TAG, "validateOldPasswordCorrect:credential is null");
            futureBool.complete(false);
            return futureBool;
        }

        Task<Void> reauthenticateTask = user.reauthenticate(credential);
        reauthenticateTask.continueWith(executor, new Continuation<Void, Void>() {
           @Override
           public Void then(@NonNull Task<Void> task) throws Exception {
               if (task.isSuccessful()) {
                   Log.d(TAG, "reAuthenticate:success");
                   futureBool.complete(true);
               } else {
                   Log.e(TAG, "reAuthenticate:failure - " + task.getException().getMessage());
                   futureBool.complete(false);
               }
               return null;
           }
        });
        return futureBool;
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
    public CompletableFuture<String> changePassword(String oldPW, String newPw, String confirmPw, FirebaseAuth mAuth) throws InterruptedException {
        CompletableFuture<String> passwordChangeResult = new CompletableFuture<>(); // make sure status is false to start process
        if (!validPassword(newPw)) {
            passwordChangeResult.complete("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
            return passwordChangeResult;
        }
        if (!validateNewPasswordMatchesConfirmPassword(newPw, confirmPw)) { // confirm new password and confirm password match before anything else
            Log.d(TAG, "changePassword:new password and confirm password do not match | newPW: " + newPw + " | confirmPW: " + confirmPw);
            passwordChangeResult.complete("New password and confirm password do not match.");
            return passwordChangeResult;
        }
        CompletableFuture<Boolean> oldPasswordCorrectResult = validateOldPasswordCorrect(oldPW, mAuth); // confirm old password is correct, and re-authenticate to allow the password change call to work
        oldPasswordCorrectResult.thenAccept(result -> {
            if (!result) {
                passwordChangeResult.complete("Old password does not match current password.");
                return;
            }
            FirebaseUser user = mAuth.getCurrentUser();
            Task<Void> updatePasswordTask = user.updatePassword(newPw);
            updatePasswordTask.continueWith(executor, new Continuation<Void, Void>() {
               @Override
               public Void then(@NonNull Task<Void> task) throws Exception {
                   if (task.isSuccessful()) {
                       Log.e(TAG, "changePassword:success");
                       passwordChangeResult.complete("Password changed successfully");
                   } else {
                       Log.e(TAG, "changePassword:failure - " + task.getException().getMessage());
                       passwordChangeResult.complete("Password change failed, please contact the development team for support.");
                   }
                   return null;
               }
               });
            });
        return passwordChangeResult;
    }
}
