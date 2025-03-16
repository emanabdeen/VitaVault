package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.UserAccount;
import com.example.insight.utility.StringHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AccountViewModel extends ViewModel {

    private final static String TAG = "AccountViewModel";
    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //get the current user
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseUser currentUser = auth.getCurrentUser();
    public String uid= currentUser.getUid();


    //set the liveData for the user details
    private final MutableLiveData<UserAccount> userAccountData = new MutableLiveData<>();
    UserAccount userAccount = new UserAccount();

    private final MutableLiveData<String> resultMessageData = new MutableLiveData<>();
    String resultMessage = "";

    public AccountViewModel(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
        userAccountData.postValue(userAccount);
        resultMessageData.postValue(resultMessage);
    }

    public AccountViewModel() {
        userAccountData.postValue(userAccount);
        resultMessageData.postValue(resultMessage);
    }

    public LiveData<UserAccount> getUserAccountData() {
        return userAccountData;
    }

    public CompletableFuture<Boolean> GetUserProfile(){
        CompletableFuture<Boolean> userDataRetrieved = new CompletableFuture<>();

        // Reference
        DocumentReference docRef = db
                .collection("users")
                .document(uid);

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        userAccount = new UserAccount();

                        // Document exists, retrieve the data
                        Map<String, Object> document = documentSnapshot.getData();

                        Log.d("debug", "user gender: " + document.get("Gender"));
                        Log.d("debug", "user age: " + document.get("AgeRange"));

                        try {
                            String userGender = StringHandler.defaultIfNull(document.get("Gender"));
                            String UserAge = StringHandler.defaultIfNull(document.get("AgeRange"));
                            userAccount.setGender(userGender);
                            userAccount.setAgeRange(UserAge);
                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                            userDataRetrieved.complete(false);
                        }

                        userAccountData.postValue(userAccount);
                        userDataRetrieved.complete(true);
                    } else {
                        // Document does not exist
                        userAccountData.postValue(null);
                        Log.e("Error", "No such document");
                        userDataRetrieved.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Error", "Error getting document: " + e.getMessage());
                    userAccountData.postValue(null);
                    userDataRetrieved.complete(false);
                });
        return userDataRetrieved;
    }

    public CompletableFuture<Boolean> UpdateUserData(UserAccount updatedUserAccount) {
        CompletableFuture<Boolean> updateCompleted = new CompletableFuture<>();

        // Reference to the user's document
        DocumentReference docRef = db
                .collection("users")
                .document(uid);

        // Create a Map to store the updated fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("Gender", updatedUserAccount.getGender());
        updates.put("AgeRange", updatedUserAccount.getAgeRange());

        // Perform the update
        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateUserData", "User data updated successfully");

                    resultMessageData.postValue("Updated successfully.");
                    userAccount =updatedUserAccount;
                    userAccountData.postValue(updatedUserAccount);
                    updateCompleted.complete(true); // Update successful
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateUserData", "Error updating user data: " + e.getMessage());
                    resultMessageData.postValue("Error updating data");
                    updateCompleted.complete(false); // Update failed
                });

        return updateCompleted;
    }

}
