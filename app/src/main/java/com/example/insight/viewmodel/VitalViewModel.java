package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.insight.model.Symptom;
import com.example.insight.model.Vital;
import com.example.insight.utility.Unites;
import com.example.insight.utility.VitalsCategories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VitalViewModel {

    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //get the current user
    FirebaseAuth auth =FirebaseAuth.getInstance();
    public FirebaseUser currentUser = auth.getCurrentUser();
    public String uid= currentUser.getUid();

    //set the LiveData
    private final MutableLiveData<List<Vital>> vitalsData = new MutableLiveData<>();
    List<Vital> vitalsList = new ArrayList<>();

    //set the liveData for the vital details
    private final MutableLiveData<Vital> selectedVitalData = new MutableLiveData<>();
    Vital selectedVital = new Vital();

    //set the liveData for searchResultMessageData
    private final MutableLiveData<String> searchResultMessageData = new MutableLiveData<>();
    String searchResultMessage = "";

    //constructor
    public VitalViewModel() {
        vitalsData.setValue(vitalsList);
        selectedVitalData.setValue(selectedVital);
        searchResultMessageData.setValue(searchResultMessage);
    }

    //Getter methods
    public LiveData<List<Vital>> getVitalsData() {
        return vitalsData;
    }
    public LiveData<Vital> getSelectedVitalData() {
        return selectedVitalData;
    }
    public LiveData<String> getSearchResultMessageData() {
        return searchResultMessageData;
    }


    public void AddVital(Vital vital){

        // Extract details from LocalDate
        LocalDate recordDate = vital.getRecordDate();

        String recordDateStr = vital.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String recordTimeStr = vital.getRecordTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String vitalType = vital.getVitalType();
        String measurement1 = vital.getMeasurement1();
        String measurement2 = vital.getMeasurement2();
        String unit = vital.getUnit();



        // Create a map to hold the details of the document under the symptoms collection
        Map<String, Object> vitalDetails = new HashMap<>();
        vitalDetails.put("recordDate", recordDateStr);
        vitalDetails.put("recordTime", recordTimeStr);
        vitalDetails.put("vitalType", vitalType);
        vitalDetails.put("measurement1", measurement1);
        vitalDetails.put("measurement2", measurement2);
        vitalDetails.put("unit",unit);

        try {
            DocumentReference addedDocRef = db.collection("users")
                    .document(uid)
                    .collection("vitals")
                    .add(vitalDetails).getResult();

            String generatedId = addedDocRef.getId(); //get the iD of the created document
            Log.d("debug", "Document added with ID: " + generatedId);

        } catch (Exception e) {
            Log.e("Firestore", "Error adding document: " + e.getMessage());
        }

    }

    public void GetVitalsByDate(LocalDate searchDate) {

        String searchDateStr = searchDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Reference to the user's symptoms collection
        CollectionReference vitalsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        // Create a query to find documents that match the specified criteria
        Query query = vitalsRef
                .whereEqualTo("recordDate", searchDateStr);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    vitalsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("debug", "----------------------GetObjectsByDate--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "vital Type: " + document.getString("vitalType"));
                            Log.d("debug", "unit: " + document.getString("unit"));
                            Log.d("debug", "measurement1: " + document.getString("measurement1"));
                            Log.d("debug", "measurement2: " + document.getString("measurement2"));
                            Log.d("debug", "record Time: " + document.getString("recordTime"));
                            Log.d("debug", "record Date: " + document.getString("recordDate"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String vitalTypeStr = document.getString("vitalType");
                                String measurement1 = document.getString("measurement1");
                                String measurement2 = document.getString("measurement2");
                                String unitStr = document.getString("unit");
                                String recordTimeStr = document.getString("recordTime");

                                LocalTime recordTime = LocalTime.parse(recordTimeStr, DateTimeFormatter.ofPattern("HH:mm"));


                                // Create symptom object with the retrieved data
                                Vital vital = new Vital(searchDate, recordTime, vitalTypeStr,unitStr);
                                vital.setVitalId(document.getId());
                                vital.setMeasurement1(measurement1);
                                vital.setMeasurement2(measurement2);

                                vitalsList.add(vital);
                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d("debug", "----------------------vitalsList count>> " + vitalsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        vitalsData.postValue(vitalsList);
                    } else {

                        searchResultMessageData.postValue("No movies found matching your search.");
                        vitalsData.postValue(vitalsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    vitalsData.postValue(null);// Handle failure
                });

    }

    public void GetVitalsByType(String vitalType) {

        // Reference to the user's symptoms collection
        CollectionReference vitalsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        // Create a query to find documents that match the specified criteria
        Query query = vitalsRef
                .whereEqualTo("vitalType", vitalType);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    vitalsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("debug", "----------------------Get Vitals By type--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("debug", "Document ID: " + document.getId());
                            Log.d("debug", "vital Type: " + document.getString("vitalType"));
                            Log.d("debug", "unit: " + document.getString("unit"));
                            Log.d("debug", "measurement1: " + document.getString("measurement1"));
                            Log.d("debug", "measurement2: " + document.getString("measurement2"));
                            Log.d("debug", "record Time: " + document.getString("recordTime"));
                            Log.d("debug", "record Date: " + document.getString("recordDate"));
                            Log.d("debug", "--------------------------------------------");

                            try {
                                String vitalTypeStr = document.getString("vitalType");
                                String measurement1 = document.getString("measurement1");
                                String measurement2 = document.getString("measurement2");
                                String unitStr = document.getString("unit");
                                String recordTimeStr = document.getString("recordTime");
                                String recordDateStr = document.getString("recordDate");

                                LocalTime recordTime = LocalTime.parse(recordTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                // Create symptom object with the retrieved data
                                Vital vital = new Vital(recordDate, recordTime, vitalTypeStr,unitStr);
                                vital.setVitalId(document.getId());
                                vital.setMeasurement1(measurement1);
                                vital.setMeasurement2(measurement2);

                                vitalsList.add(vital);

                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        Log.d("debug", "----------------------vitalsList count>> " + vitalsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        vitalsData.postValue(vitalsList);
                    } else {
                        searchResultMessageData.postValue("No vitals found matching your search.");
                        vitalsData.postValue(vitalsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    vitalsData.postValue(null);// Handle failure
                });
    }

}
