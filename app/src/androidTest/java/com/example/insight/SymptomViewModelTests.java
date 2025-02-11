package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.insight.model.Symptom;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SymptomViewModelTests {
    private static final String TAG = "SymptomViewModelTests";
    private static final String TEST_ACCOUNT_EMAIL = "unittestaccount@insight.com";
    private static final String TEST_ACCOUNT_PASSWORD = "AAbbcc123!";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context appContext;
    private SymptomViewModel symptomViewModel;

    @Before
    public void setup() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2); // create a latch to wait for the task to complete
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(appContext); // Initialize Firebase App
        mAuth = FirebaseAuth.getInstance();
        mAuth.useEmulator("10.0.2.2", 9099);
        db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8080);

        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword(TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Signed in as unittestaccount");
                        latch.countDown(); // decrement the latch count
                    } else {
                        Log.d(TAG, "Sign in was not successful");
                        //fail("Sign in failed: " + task.getException().getMessage());
                        if (task.getException().getMessage() != null && task.getException().getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            Log.d(TAG, "Account does not exist, creating account.");
                            mAuth.createUserWithEmailAndPassword(TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_PASSWORD)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Account creation was successful");
                                                latch.countDown(); // decrement the latch count
                                            } else {
                                                Log.d(TAG, "Account creation was not successful");
                                                if (task.getException().getMessage() != null) {
                                                    fail("Account creation failed: " + task.getException().getMessage());
                                                }
                                            }
                                        }
                                    });
                        }
                    }

                }
            });
        }
        latch.countDown(); // decrement the latch count
        latch.await(10, TimeUnit.SECONDS); // wait for the latch to count down to zero);
    }

    @Test
    public void addSymptom_NewAccount_ExpectSymptomAdded() throws InterruptedException {
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel();
        Symptom symptom = new Symptom("testSymptom", "testLevel");
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        symptomsData.observeForever(new Observer<List<Symptom>>() {
                                        @Override
                                        public void onChanged(List<Symptom> symptoms) {
                                            if (symptoms != null && !symptoms.isEmpty()) {
                                                Log.d(TAG, "Symptoms list updated: " + symptoms.size() + "symptoms found.");
                                                for (Symptom s : symptoms) {
                                                    Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom Level: " + s.getSymptomLevel());
                                                }
                                            }
                                            latch.countDown(); // decrement the latch count
                                        }
                                    });
                symptomViewModel.AddSymptom(uid, symptom);
                latch.await(10, TimeUnit.SECONDS);
                List<Symptom> symptomsList = symptomsData.getValue();
                if (symptomsList != null && !symptomsList.isEmpty()) {
                    assertEquals(true, symptomsList.size() > 0);
                } else {
                    fail("Symptoms list is null or empty");
                }
        assertEquals(true, true);
    }
}