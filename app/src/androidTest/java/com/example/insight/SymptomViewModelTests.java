package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
    //private static final Executor executor = Executors.newSingleThreadExecutor();
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
        db = FirebaseFirestore.getInstance();
        try {
            mAuth.useEmulator("10.0.2.2", 9099);
            db.useEmulator("10.0.2.2", 8080);
        } catch (Exception e) {
            Log.e(TAG, "An exception occurred in useEmulator calls: " + e.getMessage());
        }

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

    @Test // could add more to this test so that it retrieves and validates the symptom matches.
    public void addSymptom_ExpectReturnTrue() throws InterruptedException {
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        Symptom symptom = new Symptom("testSymptom", "testLevel");
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptom);
        symptomAdded.whenComplete((result, error) -> {
            if (error != null) {
                fail("Symptom add failed: " + error.getMessage());
            }
            assertTrue(result);
        });
    }

    @Test
    public void addSymptomToPreviousDay_GetSymptomByDatePreviousDay_ExpectSymptomListIncrease() throws InterruptedException {
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CountDownLatch latch = new CountDownLatch(3);
        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        Symptom symptomToAdd = new Symptom("testSymptom", "testLevel");
        symptomToAdd.setRecordDate(LocalDate.now().minusDays(1));
        symptomViewModel.GetSymptomsByDate(LocalDate.now().minusDays(1));
        List<Symptom> symptomsList = symptomsData.getValue();
        int initialSize = symptomsList.size();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                symptomsData.observeForever(new Observer<List<Symptom>>() {
                    @Override
                    public void onChanged(List<Symptom> symptoms) {
                        if (symptoms != null && !symptoms.isEmpty()) {
                            Log.d(TAG, "Symptoms list updated: " + symptoms.size() + " symptoms found.");
                            for (Symptom s : symptoms) {
                                Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom Level: " + s.getSymptomLevel());
                            }
                        }
                        latch.countDown(); // decrement the latch count
                    }
                });
            }
        });
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptomToAdd);
        symptomAdded.whenComplete((result, error) -> {
            if (result) {
                Log.d(TAG, "Symptom added successfully");
                symptomViewModel.GetSymptomsByDate(LocalDate.now().minusDays(1));
                latch.countDown();
            } else {
                Log.d(TAG, "Symptom add failed: " + error.getMessage());
            }
        });
        latch.await(10, TimeUnit.SECONDS);
        symptomsList = symptomsData.getValue();
        if (symptomsList != null && !symptomsList.isEmpty()) {
            Log.d(TAG, "Symptoms list size: " + symptomsList.size());
            assertEquals(true, symptomsList.size() > initialSize);
        } else {
            fail("Symptoms list is null or empty");
        }
    }

    @Test
    public void addSymptomToPreviousDayWithCustomType_GetSymptomByDateAndTypePreviousDayAndCustomType_ExpectSymptomListIncrease() throws InterruptedException {
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CountDownLatch latch = new CountDownLatch(3);
        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        String expectedSymptomType = "Custom Symptom Type";
        Symptom symptomToAdd = new Symptom(expectedSymptomType, "testLevel");
        symptomToAdd.setRecordDate(LocalDate.now().minusDays(1));
        symptomViewModel.GetSymptomsByDateAndType(LocalDate.now().minusDays(1), expectedSymptomType);
        List<Symptom> symptomsList = symptomsData.getValue();
        int initialSize = symptomsList.size();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                symptomsData.observeForever(new Observer<List<Symptom>>() {
                    @Override
                    public void onChanged(List<Symptom> symptoms) {
                        if (symptoms != null && !symptoms.isEmpty()) {
                            Log.d(TAG, "Symptoms list updated: " + symptoms.size() + " symptoms found.");
                            for (Symptom s : symptoms) {
                                Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom Level: " + s.getSymptomLevel());
                            }
                        }
                        latch.countDown(); // decrement the latch count
                    }
                });
            }
        });
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptomToAdd);
        symptomAdded.whenComplete((result, error) -> {
            if (result) {
                Log.d(TAG, "Symptom added successfully");
                symptomViewModel.GetSymptomsByDateAndType(LocalDate.now().minusDays(1), expectedSymptomType);
                latch.countDown();
            } else {
                Log.d(TAG, "Symptom add failed: " + error.getMessage());
            }
        });
        latch.await(10, TimeUnit.SECONDS);
        symptomsList = symptomsData.getValue();
        if (symptomsList != null && !symptomsList.isEmpty()) {
            Log.d(TAG, "Symptoms list size: " + symptomsList.size());
            assertEquals(true, symptomsList.size() > initialSize);
        } else {
            fail("Symptoms list is null or empty");
        }
    }

    @Test
    public void addSymptomWithType_GetSymptomsByType_ExpectListIncrease() throws InterruptedException {
        // Arrange
        String expectedSymptomType = "testDifferentType";
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        Symptom symptom = new Symptom(expectedSymptomType, "testLevel");
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CountDownLatch latch = new CountDownLatch(3);
        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        symptomViewModel.GetSymptomsByType(expectedSymptomType);
        List<Symptom> symptomsList = symptomsData.getValue();
        int initialSize = symptomsList.size();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                symptomsData.observeForever(new Observer<List<Symptom>>() {
                    @Override
                    public void onChanged(List<Symptom> symptoms) {
                        if (symptoms != null && !symptoms.isEmpty()) {
                            Log.d(TAG, "Symptoms list updated: " + symptoms.size() + " symptoms found.");
                            for (Symptom s : symptoms) {
                                Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom Level: " + s.getSymptomLevel());
                            }
                        }
                        latch.countDown(); // decrement the latch count
                    }
                });
            }
        });
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptom);
        symptomAdded.whenComplete((result, error) -> {
            if (result) {
                Log.d(TAG, "Symptom added successfully");
                symptomViewModel.GetSymptomsByType(expectedSymptomType);
                latch.countDown();
            } else {
                Log.d(TAG, "Symptom add failed: " + error.getMessage());
            }
        });
        latch.await(10, TimeUnit.SECONDS);
        symptomsList = symptomsData.getValue();
        if (symptomsList != null && !symptomsList.isEmpty()) {
            Log.d(TAG, "Symptoms list size: " + symptomsList.size());
            assertEquals(true, symptomsList.size() > initialSize);
        } else {
            fail("Symptoms list is null or empty");
        }
    }

    @Test
    public void addNewSymptom_GetSymptomById_ExpectSymptomFound() throws InterruptedException { //Need to add symptom to make sure there is a symptom to retrieve first
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        FirebaseUser user = mAuth.getCurrentUser();
        final String[] lastAddedSymptomId = {};

        String uid = user.getUid(); // Get the logged-in user's unique ID
        String expectedSymptomType = "GetSymptomById-Type";
        String expectedSymptomLevel = "GetSymptomById-Level";
        String expectedSymptomDescription = UUID.randomUUID().toString();
        Symptom symptomToAdd = new Symptom(expectedSymptomType, expectedSymptomLevel, expectedSymptomDescription);

        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        LiveData<Symptom> selectedSymptomData = symptomViewModel.getSelectedSymptomData();
        //
        // Add symptom
        //CountDownLatch addLatch = new CountDownLatch(1);
        //symptomAdded.whenComplete((result, error) -> {
//            if (result) {
//                Log.d(TAG, "Symptom added successfully");
//                Log.d("debug", "Symptom Name: " + symptomToAdd.getSymptomName());
//                Log.d("debug", "Symptom Level: " + symptomToAdd.getSymptomLevel());
//                Log.d("debug", "Record Date: " + symptomToAdd.getRecordDate()); //format different between local and stored
//                Log.d("debug", "Start Time: " + symptomToAdd.getStartTime()); //format different between local and stored
//                Log.d("debug", "End Time: " + symptomToAdd.getEndTime()); //format different between local and stored
//                Log.d("debug", "Description: " + symptomToAdd.getSymptomDescription());
//                Log.d("debug", "--------------------------------------------");
//                symptomViewModel.GetSymptomsByType(expectedSymptomType);
//                addLatch.countDown();
//            } else {
//                Log.d(TAG, "Symptom add failed: " + error.getMessage());
//            }
//        });
        //addLatch.await(10, TimeUnit.SECONDS);
        // Get symptoms of expectedSymptomType
        CountDownLatch getSymptomsByTypeLatch = new CountDownLatch(3);
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptomToAdd)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        fail("Symptom add failed: " + error.getMessage());
                    }
                    if (result) {
                        Log.d(TAG, "Symptom added successfully");
                    }
                    getSymptomsByTypeLatch.countDown();
        });
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                symptomsData.observeForever(new Observer<List<Symptom>>() {
                    @Override
                    public void onChanged(List<Symptom> symptoms) {
                        if (symptoms != null) {
                            Log.d(TAG, "Selected symptom updated: " + symptoms.size() + " symptoms found.");
                            for (Symptom s : symptoms) {
                                Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom description: " + s.getSymptomDescription());
                            }
                        } else {
                          Log.d(TAG, "No symptoms found");
                        }
                        getSymptomsByTypeLatch.countDown(); // decrement the latch count
                    }
                });
            }
        });
        CompletableFuture<Boolean> symptomListRetrieved = symptomViewModel.GetSymptomsByType(expectedSymptomType)
                .whenComplete((result, error) -> {
                    if (result) {
                        Log.d(TAG, "Symptom list retrieved by type successfully");
                        if (symptomsData.getValue() == null && symptomsData.getValue().isEmpty()) {
                            fail("Symptoms List was empty, symptom add failed but returned true?");
                        }
                        for (Symptom s : symptomsData.getValue()) { //Find last added symptom
                            Log.d(TAG, "Symptom Name: " + s.getSymptomName() + " | Symptom Level: " + s.getSymptomLevel() + " | Symptom ID: " + s.getSymptomId());
                            if (s.getSymptomDescription().equals(symptomToAdd.getSymptomDescription())) {
                                Log.d(TAG, "Found last added symptom: " + s.getSymptomName());
                                lastAddedSymptomId[0] = s.getSymptomId();
                            }
                        }
                        if (lastAddedSymptomId[0].isEmpty()) {
                            fail("Last added symptom not found");
                        }
                        getSymptomsByTypeLatch.countDown();
                    } else {
                        Log.d(TAG, "Symptom list retrieval by type failed: " + error.getMessage());
                        fail("Symptom list retrieval by type failed");
                    }
                    CompletableFuture<Boolean> symptomRetrievedSuccessfully = symptomViewModel.GetSymptomById(lastAddedSymptomId[0]).
                            whenComplete((symptomIdRetrieval, symptomIdError) -> {
                                if (result) {
                                    Log.d(TAG, "Symptom retrieved successfully by ID");
                                    Symptom retrievedSymptom = selectedSymptomData.getValue();
                                    if (retrievedSymptom == null) {
                                        fail("Retrieved symptom was null");
                                    }
                                    assertEquals(expectedSymptomType, retrievedSymptom.getSymptomName());
                                    assertEquals(expectedSymptomLevel, retrievedSymptom.getSymptomLevel());
                                    assertEquals(symptomToAdd.getRecordDate(), retrievedSymptom.getRecordDate());
                                    assertEquals(symptomToAdd.getStartTime(), retrievedSymptom.getStartTime());
                                    assertEquals(symptomToAdd.getEndTime(), retrievedSymptom.getEndTime());
                                    assertEquals(symptomToAdd.getSymptomDescription(), retrievedSymptom.getSymptomDescription());
                                } else {
                                    Log.d(TAG, "Symptom retrieval by ID failed: " + error.getMessage());
                                    fail("Symptom retrieval by ID failed");
                                }
                            });
        });
    }

    @Test
    public void addNewSymptomOnCurrentDate_GetSymptomsByDateCurrentDate_ExpectListIncrease() throws InterruptedException {
        // Arrange
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        Symptom symptom = new Symptom("testSymptom", "testLevel");
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        CountDownLatch latch = new CountDownLatch(3);
        LiveData<List<Symptom>> symptomsData = symptomViewModel.getSymptomsData();
        symptomViewModel.GetSymptomsByDate(LocalDate.now());
        List<Symptom> symptomsList = symptomsData.getValue();
        int initialSize = symptomsList.size();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                symptomsData.observeForever(new Observer<List<Symptom>>() {
                    @Override
                    public void onChanged(List<Symptom> symptoms) {
                        if (symptoms != null && !symptoms.isEmpty()) {
                            Log.d(TAG, "Symptoms list updated: " + symptoms.size() + " symptoms found.");
                            for (Symptom s : symptoms) {
                                Log.d(TAG, "Symptom Name: " + s.getSymptomName() + ", Symptom Level: " + s.getSymptomLevel());
                            }
                        }
                        latch.countDown(); // decrement the latch count
                    }
                });
            }
        });
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptom);
        symptomAdded.whenComplete((result, error) -> {
            if (result) {
                Log.d(TAG, "Symptom added successfully");
                symptomViewModel.GetSymptomsByDate(LocalDate.now());
                latch.countDown();
            } else {
                Log.d(TAG, "Symptom add failed: " + error.getMessage());
            }
        });
        latch.await(10, TimeUnit.SECONDS);
        symptomsList = symptomsData.getValue();
        if (symptomsList != null && !symptomsList.isEmpty()) {
            Log.d(TAG, "Symptoms list size: " + symptomsList.size());
            assertEquals(true, symptomsList.size() > initialSize);
        } else {
            fail("Symptoms list is null or empty");
        }
    }

    // NOT WORKING, MAYBE RELATED TO EMULATOR, SAME SITUATION AS OTHER NOT WORKING ONE
    @Test
    public void addSymptom_UserNotSignedIn_ExpectReturnFalse() throws InterruptedException {
        // Arrange
        boolean expected = false;
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        Symptom symptom = new Symptom("testSymptom", "testLevel");
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid(); // Get the logged-in user's unique ID
        mAuth.signOut();
        Log.d(TAG, "Signed out of unittestaccount");
        CompletableFuture<Boolean> symptomAdded = symptomViewModel.AddSymptom(uid, symptom);
        symptomAdded.whenComplete((result, error) -> {
            if (error != null) {
                fail("Symptom add failed: " + error.getMessage());
            }
            assertEquals(expected, result);
        });
    }

    @Test // THIS ONE ISN'T WORKING YET
    public void getSymptomsByDate_Tomorrow_ExpectEmptyList() throws InterruptedException {
        // Arrange
        String expectedMessage = "No symptoms found matching this date";
        SymptomViewModel symptomViewModel = new SymptomViewModel(this.db, this.mAuth);
        // Act
        LiveData<String> searchResultString = symptomViewModel.getSearchResultMessageData();
        symptomViewModel.GetSymptomsByDate(LocalDate.now());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchResultString.observeForever(new Observer<String>() {
                    @Override
                    public void onChanged(String resultMessage) {
                        if (resultMessage != null && !resultMessage.isEmpty()) {
                            Log.d(TAG, "Search result message: " + resultMessage);
                            // Assert
                            assertTrue(resultMessage.contains(expectedMessage));
                        }
                    }
                });
            }
        });
    }

}