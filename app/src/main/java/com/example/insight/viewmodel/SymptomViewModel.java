package com.example.insight.viewmodel;

import android.util.Log;

import com.example.insight.model.CoughingSymptom;
import com.example.insight.model.NauseaSymptom;
import com.example.insight.model.OtherSymptom;
import com.example.insight.model.Symptom;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public void AddSymptom( String uid, Symptom symptom){

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


    public void searchSymptomsByDate(String uid, LocalDate searchDate, OnSymptomsRetrievedListener listener) {
        // Extract search date details
        int dayOfMonth = searchDate.getDayOfMonth();
        int monthValue = searchDate.getMonthValue();
        int year = searchDate.getYear();

        // Reference to the symptoms collection
        db.collection("users")
                .document(uid)
                .collection("symptomsDate")
                .whereEqualTo("dayOfMonth", dayOfMonth)
                .whereEqualTo("monthValue", monthValue)
                .whereEqualTo("year", year)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            fetchSymptoms(document.getReference(), searchDate, listener);
                            return; // Assuming one symptom document per date, exit loop after finding one
                        }
                    } else {
                        Log.e("Firestore", "No matching document found or error: " + (task.getException() != null ? task.getException().getMessage() : "No document found"));
                        listener.onSymptomsRetrieved(null);
                    }
                });
    }

    public void fetchSymptoms(DocumentReference dateRef, LocalDate searchDate, OnSymptomsRetrievedListener listener) {

        dateRef.collection("symptoms")
                .get()
                .addOnCompleteListener(taskS -> {
                    if (taskS.isSuccessful()) {
                        List<Symptom> symptoms = new ArrayList<>();
                        for (DocumentSnapshot document : taskS.getResult()) {
                            String symptomName = document.getString("symptomName");
                            String symptomLevel = document.getString("symptomLevel");
                            String symptomDescription = document.getString("symptomDescription");
                            String startTimeStr = document.getString("startTime");
                            String endTimeStr = document.getString("endTime");

                            try {
                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptoms.add(symptom);
                            } catch (DateTimeParseException e) {
                                Log.e("Firestore", "Error parsing time: " + e.getMessage());
                            }
                        }

                        // Notify listener with the retrieved symptoms
                        listener.onSymptomsRetrieved(symptoms);
                    } else {
                        Log.e("Firestore", "Error fetching subcollection: " + taskS.getException().getMessage());
                        listener.onSymptomsRetrieved(null); // Or an empty list if you prefer
                    }
                });
    }


    // Interface for callback when symptoms are retrieved
    public interface OnSymptomsRetrievedListener {
        void onSymptomsRetrieved(List<Symptom> symptoms);
    }


}
