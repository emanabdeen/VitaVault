package com.example.insight.viewmodel;

import android.util.Log;

import com.example.insight.model.CoughingSymptom;
import com.example.insight.model.NauseaSymptom;
import com.example.insight.model.OtherSymptom;
import com.example.insight.model.Symptom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import kotlinx.coroutines.channels.ActorKt;


public class SymptomViewModel {
    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //constructor
    public SymptomViewModel() { }

    //Add symptom
    public void AddSymptomOld( String uid, Symptom symptom){

        // Extract details from LocalDate
        LocalDate recordDate = symptom.getRecordDate();
        int dayOfMonth = recordDate.getDayOfMonth();
        String monthName = recordDate.getMonth().name(); // e.g., "JANUARY"
        int monthNumber = recordDate.getMonthValue(); // e.g., 1 for January
        int year = recordDate.getYear();

        // Create an ID for the symptoms document based on the date
        String symptID = "symp" + recordDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        // Create a map to hold only the necessary date details
        Map<String, Object> symptomDate = new HashMap<>();
        symptomDate.put("dayOfMonth", dayOfMonth);
        symptomDate.put("monthName", monthName);
        symptomDate.put("monthValue", monthNumber);
        symptomDate.put("year", year);

        // Reference to the Firestore document
        DocumentReference symptomRef = db.collection("users")
                .document(uid)
                .collection("symptomsDate")
                .document(symptID);

        // Save the symptom date to Firestore
        symptomRef.set(symptomDate)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Added Successfully ✔"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add: " + e.getMessage()));


        // Ensure `recordDate` and `startTime` are properly formatted
        String recordDateStr = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String startTimeStr = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeStr = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String symptomName = symptom.getSymptomName();
        String symptomLevel = symptom.getSymptomLevel();
        String symptomDescription = symptom.getSymptomDescription();


        // Create a map to hold symptom details
        Map<String, Object> symptomDetails = new HashMap<>();
        symptomDetails.put("recordDate", recordDateStr);
        symptomDetails.put("startTime", startTimeStr);
        symptomDetails.put("endTime", endTimeStr);

        symptomDetails.put("symptomName", symptomName);
        symptomDetails.put("symptomLevel", symptomLevel);
        symptomDetails.put("symptomDescription", symptomDescription);

        String symptomTypeID = symptomName + symptom.getStartTime().format(DateTimeFormatter.ofPattern("HHmm"));
        symptomRef = symptomRef.collection("symptoms").document(symptomTypeID);


        // Save the symptom data to Firestore
        symptomRef.set(symptomDetails)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Added Successfully ✔"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add: " + e.getMessage()));

    }

    public void AddSymptom( String uid, Symptom symptom){

        // Extract details from LocalDate
        LocalDate recordDate = symptom.getRecordDate();

        int dayOfMonth = recordDate.getDayOfMonth();
        String monthName = recordDate.getMonth().name(); // e.g., "JANUARY"
        int monthNumber = recordDate.getMonthValue(); // e.g., 1 for January
        int year = recordDate.getYear();

        String recordDateStr = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String startTimeStr = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeStr = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String symptomName = symptom.getSymptomName();
        String symptomLevel = symptom.getSymptomLevel();
        String symptomDescription = symptom.getSymptomDescription();


        // Create a map to hold the details of the document under the symptoms collection
        Map<String, Object> symptomDetails = new HashMap<>();
        symptomDetails.put("dayOfMonth", dayOfMonth);
        symptomDetails.put("monthName", monthName);
        symptomDetails.put("monthValue", monthNumber);
        symptomDetails.put("year", year);
        symptomDetails.put("recordDate", recordDateStr);
        symptomDetails.put("startTime", startTimeStr);
        symptomDetails.put("endTime", endTimeStr);
        symptomDetails.put("symptomName", symptomName);
        symptomDetails.put("symptomLevel", symptomLevel);
        symptomDetails.put("symptomDescription", symptomDescription);

        try {
            DocumentReference addedDocRef = db.collection("users")
                    .document(uid)
                    .collection("symptoms")
                    .add(symptomDetails).getResult();

            String generatedId = addedDocRef.getId(); //get the iD of the created document
            Log.d("debug", "Document added with ID: " + generatedId);

        } catch (Exception e) {
            Log.e("Firestore", "Error adding document: " + e.getMessage());
        }

    }

    public void searchSymptomsByDate(String uid, LocalDate searchDate, OnSymptomsRetrievedListener listener) {

        // Extract search date details
        int dayOfMonth = searchDate.getDayOfMonth();
        int monthValue = searchDate.getMonthValue();
        int year = searchDate.getYear();

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("dayOfMonth", dayOfMonth)
                .whereEqualTo("monthValue", monthValue)
                .whereEqualTo("year", year);

        // Execute the query using Firebase Task API
        Task<QuerySnapshot> task = query.get();

        // Add an OnCompleteListener to handle the task result
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Symptom> symptoms = new ArrayList<>();
                    if (querySnapshot != null) {
                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "Symptom Name: " + document.getString("symptomName"));
                            Log.d("debug", "Symptom Level: " + document.getString("symptomLevel"));
                            Log.d("debug", "Record Date: " + document.getString("recordDate"));
                            Log.d("debug", "Start Time: " + document.getString("startTime"));
                            Log.d("debug", "End Time: " + document.getString("endTime"));
                            Log.d("debug", "Description: " + document.getString("symptomDescription"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String symptomName = document.getString("symptomName");
                                String symptomLevel = document.getString("symptomLevel");
                                String symptomDescription = document.getString("symptomDescription");
                                String startTimeStr = document.getString("startTime");
                                String endTimeStr = document.getString("endTime");
                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptoms.add(symptom);

                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        listener.onSymptomsRetrieved(symptoms);
                    }else {
                        listener.onSymptomsRetrieved(null); // Or an empty list if you prefer
                    }
                } else {
                    System.err.println("Error retrieving documents: " + task.getException().getMessage());
                }
            }
        });
    }

    // Interface for callback when symptoms are retrieved
    public interface OnSymptomsRetrievedListener {
        void onSymptomsRetrieved(List<Symptom> symptoms);
    }


}
