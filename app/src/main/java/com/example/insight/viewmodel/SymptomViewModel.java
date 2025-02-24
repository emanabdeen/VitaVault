package com.example.insight.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.Symptom;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.TimeValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class SymptomViewModel extends ViewModel {
    private final static String TAG = "ViewModel";
    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //get the current user
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseUser currentUser = auth.getCurrentUser();
    public String uid= currentUser.getUid();

    //set the LiveData
    private final MutableLiveData<List<Symptom>> symptomsData = new MutableLiveData<>();
    List<Symptom> symptomsList = new ArrayList<>();

    //set the liveData for the Symptom details
    private final MutableLiveData<Symptom> selectedSymptomData = new MutableLiveData<>();
    Symptom selectedSymptom = new Symptom();

    //set the liveData for searchResultMessageData
    private final MutableLiveData<String> searchResultMessageData = new MutableLiveData<>();
    String searchResultMessage = "";

    //constructor to allow tests to use emulator db
    public SymptomViewModel(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.auth = mAuth;
        symptomsData.postValue(symptomsList);
        selectedSymptomData.postValue(selectedSymptom);
        searchResultMessageData.postValue(searchResultMessage);
    }

    //constructor
    public SymptomViewModel() {
        symptomsData.postValue(symptomsList);
        selectedSymptomData.postValue(selectedSymptom);
        searchResultMessageData.postValue(searchResultMessage);
    }

    //Getter methods
    public LiveData<List<Symptom>> getSymptomsData() {
        return symptomsData;
    }
    public LiveData<Symptom> getSelectedSymptomData() {
        return selectedSymptomData;
    }
    public LiveData<String> getSearchResultMessageData() {
        return searchResultMessageData;
    }


    public CompletableFuture<Boolean> AddSymptom(String uid, Symptom symptom){
        CompletableFuture<Boolean> symptomAdded = new CompletableFuture<>();
        
        // Create a map to hold the details of the document under the symptoms collection
        Map<String, Object> symptomDetails = new HashMap<>();
        try {
            //LocalDate recordDate = (symptom.getRecordDate() != null) ? symptom.getRecordDate() : LocalDate.now();
            LocalDate recordDate = symptom.getRecordDate();
            
            String recordDateStr = DateValidator.LocalDateToString(symptom.getRecordDate());
            String startTimeStr = TimeValidator.LocalTimeToString(symptom.getStartTime());
            String endTimeStr = TimeValidator.LocalTimeToString(symptom.getEndTime());
            String symptomName = symptom.getSymptomName();
            String symptomLevel = symptom.getSymptomLevel();
            String symptomDescription = symptom.getSymptomDescription();

            symptomDetails.put("recordDate", recordDateStr);
            symptomDetails.put("startTime", startTimeStr);
            symptomDetails.put("endTime", endTimeStr);
            symptomDetails.put("symptomName", symptomName);
            symptomDetails.put("symptomLevel", symptomLevel);
            symptomDetails.put("symptomDescription", symptomDescription);

        }catch(Exception e) {
            Log.e("Error", "Error mapping data: " + e.getMessage());
        }

        try {
            final String[] generatedId = new String[1];// = addedDocRef.getId(); //get the iD of the created document
            db.collection("users")
                    .document(uid)
                    .collection("symptoms")
                    .add(symptomDetails).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                generatedId[0] = task.getResult().getId();
                                Log.d(TAG, "Document added with ID: " + generatedId[0]);
                                symptomAdded.complete(true);
                            } else {
                                Log.d(TAG, "Error adding document: " + task.getException().getMessage());
                                symptomAdded.complete(false);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error adding document: " + e.getMessage());
                            symptomAdded.complete(false);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Firestore: Error adding document: " + e.getMessage());
            symptomAdded.complete(false);
        }
        if (!symptomAdded.isDone()) {
            Log.e(TAG, "Error: The task never completed.");
            symptomAdded.complete(false);
        }
        return symptomAdded; //update the symptomslist livedata?
    }

    public void GetSymptomsByDate(String searchDateStr) {

        LocalDate searchDate = DateValidator.StringToLocalDate(searchDateStr); 

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("recordDate", searchDateStr);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    symptomsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d(TAG, "----------------------GetSymptomsByDate--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "Symptom Name: " + document.get("symptomName"));
                            Log.d(TAG, "Symptom Level: " + document.get("symptomLevel"));
                            Log.d(TAG, "Record Date: " + document.get("recordDate"));
                            Log.d(TAG, "Start Time: " + document.get("startTime"));
                            Log.d(TAG, "End Time: " + document.get("endTime"));
                            Log.d(TAG, "Description: " + document.get("symptomDescription"));
                            Log.d(TAG, "--------------------------------------------");

                            try {
                                String symptomName = StringHandler.defaultIfNull(document.get("symptomName"));
                                String symptomLevel = StringHandler.defaultIfNull(document.get("symptomLevel"));
                                String symptomDescription = StringHandler.defaultIfNull(document.get("symptomDescription"));
                                String startTimeStr = StringHandler.defaultIfNull(document.get("startTime"));
                                String endTimeStr = StringHandler.defaultIfNull(document.get("endTime"));

                                LocalTime startTime = TimeValidator.StringToLocalTime(startTimeStr);
                                LocalTime endTime = TimeValidator.StringToLocalTime(endTimeStr);

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptomsList.add(symptom);
                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d("debug", "----------------------symptomList count>> " + symptomsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        symptomsData.postValue(symptomsList);
                    } else {
                        searchResultMessageData.postValue("No symptoms found matching this date.");
                        symptomsData.postValue(symptomsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore: Error retrieving documents: " + e.getMessage());
                    symptomsData.postValue(null);// Handle failure
                });

    }

    public void GetSymptomsByDateAndType(String searchDateStr, String symptomType) {

        LocalDate searchDate = DateValidator.StringToLocalDate(searchDateStr);

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("recordDate", searchDateStr)
                .whereEqualTo("symptomName", symptomType);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    symptomsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("debug", "----------------------Get Symptoms By Date & Type--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "Symptom Name: " + document.get("symptomName"));
                            Log.d("debug", "Symptom Level: " + document.get("symptomLevel"));
                            Log.d("debug", "Record Date: " + document.get("recordDate"));
                            Log.d("debug", "Start Time: " + document.get("startTime"));
                            Log.d("debug", "End Time: " + document.get("endTime"));
                            Log.d("debug", "Description: " + document.get("symptomDescription"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String symptomName = StringHandler.defaultIfNull(document.get("symptomName"));
                                String symptomLevel = StringHandler.defaultIfNull(document.get("symptomLevel"));
                                String symptomDescription = StringHandler.defaultIfNull(document.get("symptomDescription"));
                                String startTimeStr = StringHandler.defaultIfNull(document.get("startTime"));
                                String endTimeStr = StringHandler.defaultIfNull(document.get("endTime"));

                                LocalTime startTime = TimeValidator.StringToLocalTime(startTimeStr);
                                LocalTime endTime = TimeValidator.StringToLocalTime(endTimeStr);

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptomsList.add(symptom);
                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d("debug", "----------------------symptomList count>> " + symptomsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        symptomsData.postValue(symptomsList);
                    } else {
                        searchResultMessageData.postValue("No symptoms found....");
                        symptomsData.postValue(symptomsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    symptomsData.postValue(null);// Handle failure
                });
    }

    public void GetSymptomsByDateRange(String startDateStr, String endDateStr) {

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereGreaterThanOrEqualTo("recordDate", startDateStr)
                .whereLessThanOrEqualTo("recordDate", endDateStr);


        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    symptomsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("debug", "----------------------Get Symptoms By Date Range--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "Symptom Name: " + document.get("symptomName"));
                            Log.d("debug", "Symptom Level: " + document.get("symptomLevel"));
                            Log.d("debug", "Record Date: " + document.get("recordDate"));
                            Log.d("debug", "Start Time: " + document.get("startTime"));
                            Log.d("debug", "End Time: " + document.get("endTime"));
                            Log.d("debug", "Description: " + document.get("symptomDescription"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String symptomName = StringHandler.defaultIfNull(document.get("symptomName"));
                                String symptomLevel = StringHandler.defaultIfNull(document.get("symptomLevel"));
                                String symptomDescription = StringHandler.defaultIfNull(document.get("symptomDescription"));
                                String startTimeStr = StringHandler.defaultIfNull(document.get("startTime"));
                                String endTimeStr = StringHandler.defaultIfNull(document.get("endTime"));
                                String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));

                                LocalTime startTime = TimeValidator.StringToLocalTime(startTimeStr);
                                LocalTime endTime = TimeValidator.StringToLocalTime(endTimeStr);
                                LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptomsList.add(symptom);

                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d("debug", "----------------------symptomList count>> " + symptomsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        symptomsData.postValue(symptomsList);
                    } else {
                        searchResultMessageData.postValue("No symptoms found...");
                        symptomsData.postValue(symptomsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    symptomsData.postValue(null);// Handle failure
                });
    }

    public CompletableFuture<Boolean> GetSymptomsByType(String symptomType) {
        CompletableFuture<Boolean> symptomsRetrieved = new CompletableFuture<>();
        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("symptomName", symptomType);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    symptomsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("debug", "----------------------Get Symptoms By type--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "Symptom Name: " + document.get("symptomName"));
                            Log.d("debug", "Symptom Level: " + document.get("symptomLevel"));
                            Log.d("debug", "Record Date: " + document.get("recordDate"));
                            Log.d("debug", "Start Time: " + document.get("startTime"));
                            Log.d("debug", "End Time: " + document.get("endTime"));
                            Log.d("debug", "Description: " + document.get("symptomDescription"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String symptomName = StringHandler.defaultIfNull(document.get("symptomName"));
                                String symptomLevel = StringHandler.defaultIfNull(document.get("symptomLevel"));
                                String symptomDescription = StringHandler.defaultIfNull(document.get("symptomDescription"));
                                String startTimeStr = StringHandler.defaultIfNull(document.get("startTime"));
                                String endTimeStr = StringHandler.defaultIfNull(document.get("endTime"));
                                String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));

                                LocalTime startTime = TimeValidator.StringToLocalTime(startTimeStr);
                                LocalTime endTime = TimeValidator.StringToLocalTime(endTimeStr);
                                LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptomsList.add(symptom);

                            } catch (DateTimeParseException e) {
                                Log.e(TAG, "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d(TAG, "----------------------symptomList count>> " + symptomsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        symptomsData.postValue(symptomsList);
                        symptomsRetrieved.complete(true);
                    } else {
                        searchResultMessageData.postValue("No symptoms found...");
                        symptomsData.postValue(symptomsList);
                        symptomsRetrieved.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving documents: " + e.getMessage());
                    symptomsData.postValue(null);// Handle failure
                    symptomsRetrieved.complete(false);
                });
        return symptomsRetrieved;
    }

    public CompletableFuture<Boolean> GetSymptomById(String symptomId) {
        CompletableFuture<Boolean> symptomRetrieved = new CompletableFuture<>();
        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("symptoms");


        DocumentReference docRef = symptomsRef.document(symptomId);

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        selectedSymptom = new Symptom();

                        // Document exists, retrieve the data
                        Map<String, Object> document = documentSnapshot.getData();

                        Log.d("debug", "Document ID: " + symptomId);
                        Log.d("debug", "Symptom Name: " + document.get("symptomName"));
                        Log.d("debug", "Symptom Level: " + document.get("symptomLevel"));
                        Log.d("debug", "Record Date: " + document.get("recordDate"));
                        Log.d("debug", "Start Time: " + document.get("startTime"));
                        Log.d("debug", "End Time: " + document.get("endTime"));
                        Log.d("debug", "Description: " + document.get("symptomDescription"));
                        Log.d("debug", "--------------------------------------------");

                        try {
                            String symptomName = StringHandler.defaultIfNull(document.get("symptomName"));
                            String symptomLevel = StringHandler.defaultIfNull(document.get("symptomLevel"));
                            String symptomDescription = StringHandler.defaultIfNull(document.get("symptomDescription"));
                            String startTimeStr = StringHandler.defaultIfNull(document.get("startTime"));
                            String endTimeStr = StringHandler.defaultIfNull(document.get("endTime"));
                            String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));

                            LocalTime startTime = TimeValidator.StringToLocalTime(startTimeStr);
                            LocalTime endTime = TimeValidator.StringToLocalTime(endTimeStr);
                            LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);

                            //create symptom object with the retrieved data
                            selectedSymptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                            selectedSymptom.setSymptomId(symptomId);

                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                            symptomRetrieved.complete(false);
                        }

                        searchResultMessageData.postValue("");
                        selectedSymptomData.postValue(selectedSymptom);
                        symptomRetrieved.complete(true);
                    } else {
                        // Document does not exist
                        searchResultMessageData.postValue("symptom is not found");
                        selectedSymptomData.postValue(null);
                        Log.e("Error", "No such document");
                        symptomRetrieved.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Error", "Error getting document: " + e.getMessage());
                    selectedSymptomData.postValue(null);
                    symptomRetrieved.complete(false);
                });
        return symptomRetrieved;
    }

    public void UpdateSymptom(Symptom updatedSymptom) {

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Document reference for the specific symptom to be updated
        DocumentReference docRef = symptomsRef.document(updatedSymptom.getSymptomId());

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("symptomName", updatedSymptom.getSymptomName());
        updatedData.put("symptomLevel", updatedSymptom.getSymptomLevel());
        updatedData.put("symptomDescription", updatedSymptom.getSymptomDescription());
        updatedData.put("startTime", TimeValidator.LocalTimeToString(updatedSymptom.getStartTime()));
        updatedData.put("endTime", TimeValidator.LocalTimeToString(updatedSymptom.getEndTime()));
        updatedData.put("recordDate", DateValidator.LocalDateToString(updatedSymptom.getRecordDate()));

        // Update the document in Firestore
        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("debug", "Symptom updated successfully.");
                    searchResultMessageData.postValue("Symptom updated successfully.");
                    selectedSymptom =updatedSymptom;
                    selectedSymptomData.postValue(updatedSymptom);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error updating symptom: " + e.getMessage());
                    searchResultMessageData.postValue("Error updating symptom");
                });
    }

    public void DeleteSymptom(String symptomId) {
        // Reference to the specific symptom document
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms")
                .document(symptomId);

        // Delete the document
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("debug", "Symptom deleted successfully.");
                    //searchResultMessageData.postValue("Symptom deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error deleting symptom: " + e.getMessage());
                    //searchResultMessageData.postValue("Error deleting symptom.");
                });
    }




}
