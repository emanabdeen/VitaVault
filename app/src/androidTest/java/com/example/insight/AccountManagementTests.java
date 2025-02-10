package com.example.insight;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AccountManagementTests {
    private static final String TAG = "AccountManagementTests";
    private static final String TEST_ACCOUNT_EMAIL = "unittestaccount@insight.com";
    private static final String TEST_ACCOUNT_PASSWORD = "AAbbcc123!";
    private FirebaseAuth mAuth;
    private Context appContext;
    private LoginRegisterHelper lrh;

    @Before
    public void setup() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); // create a latch to wait for the task to complete
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(appContext); // Initialize Firebase App
        mAuth = FirebaseAuth.getInstance();
        mAuth.useEmulator("10.0.2.2", 9099);
        lrh = new LoginRegisterHelper();
        mAuth.signOut(); //logout of any existing session
        latch.countDown(); // decrement the latch count
        mAuth.createUserWithEmailAndPassword(TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Account creation was successful");
                } else {
                    Log.d(TAG, "Account creation was not successful");
                    if (task.getException().getMessage() != null && task.getException().getMessage().contains("The email address is already in use by another account.")) {
                        Log.d(TAG, "Account already exists");
                    }
                }
                latch.countDown(); // decrement the latch count
            }
        });
        mAuth.signOut();
        latch.countDown(); // decrement the latch count
        latch.await(10, TimeUnit.SECONDS); // wait for the latch to count down to zero);
    }

    @Test
    public void validateOldPasswordCorrect_ExpectTrue() throws InterruptedException {
        // Arrange
        String testAccountEmail = "unittestaccount@insight.com";
        String oldPassword = "AAbbcc123!"; // current password for the account
        Boolean expected = true;
        CountDownLatch latch = new CountDownLatch(1); // create a latch to wait for the task to complete
        mAuth.signInWithEmailAndPassword(testAccountEmail, oldPassword) // login with old password
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                               if (task.isSuccessful()) {
                                                   Log.d(TAG, "Sign in was successful");
                                               } else {
                                                   Log.d(TAG, "Sign in was not successful");
                                                   fail("Sign in failed: " + task.getException().getMessage());
                                               }
                                               latch.countDown(); // decrement the latch count
                                           }
                                       }
                );
        latch.await(10, TimeUnit.SECONDS); // wait for the latch to count down to zero);
        // Act
        CompletableFuture<Boolean> actual = lrh.validateOldPasswordCorrect(oldPassword, mAuth);
        actual.thenAccept(result -> {
            // Assert
            assertEquals(expected, result);
        });
    }
}