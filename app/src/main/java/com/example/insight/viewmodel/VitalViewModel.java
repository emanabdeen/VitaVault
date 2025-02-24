package com.example.insight.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.Symptom;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.TimeValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VitalViewModel extends ViewModel {
    private final static String TAG = "ViewModel";
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
        // Create a map to hold the details of the document under the collection
        Map<String, Object> vitalDetails = new HashMap<>();
        try{
            // Extract details from LocalDate
            LocalDate recordDate = vital.getRecordDate();

            String recordDateStr = DateValidator.LocalDateToString(vital.getRecordDate());
            String recordTimeStr = TimeValidator.LocalTimeToString(vital.getRecordTime());
            String vitalType = vital.getVitalType();
            String measurement1 = vital.getMeasurement1();
            String measurement2 = vital.getMeasurement2();
            String unit = vital.getUnit();

            // Create a map to hold the details of the document under the symptoms collection

            vitalDetails.put("recordDate", recordDateStr);
            vitalDetails.put("recordTime", recordTimeStr);
            vitalDetails.put("vitalType", vitalType);
            vitalDetails.put("measurement1", measurement1);
            vitalDetails.put("measurement2", measurement2);
            vitalDetails.put("unit",unit);
        }catch(Exception e) {
            Log.e("Error", "Error mapping data: " + e.getMessage());
        }

        try {
            DocumentReference addedDocRef = db.collection("users")
                    .document(uid)
                    .collection("vitals")
                    .add(vitalDetails).getResult();

            String generatedId = addedDocRef.getId(); //get the iD of the created document
            Log.d(TAG, "Document added with ID: " + generatedId);

        } catch (Exception e) {
            Log.e("Firestore", "Error adding document: " + e.getMessage());
        }

    }

    public void GetVitalsByDate(String recordDateStr) {

        LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr); //convert from string format yyyy-MM-dd

        // Reference to the user's symptoms collection
        CollectionReference vitalsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        // Create a query to find documents that match the specified criteria
        Query query = vitalsRef
                .whereEqualTo("recordDate", recordDateStr);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    vitalsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d(TAG, "----------------------GetObjectsByDate--------------------------------" );
                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "vital Type: " + document.get("vitalType"));
                            Log.d(TAG, "unit: " + document.get("unit"));
                            Log.d(TAG, "measurement1: " + document.get("measurement1"));
                            Log.d(TAG, "measurement2: " + document.get("measurement2"));
                            Log.d(TAG, "record Time: " + document.get("recordTime"));
                            Log.d(TAG, "record Date: " + document.get("recordDate"));
                            Log.d(TAG, "--------------------------------------------");

                            try {
                                String vitalTypeStr = StringHandler.defaultIfNull(document.get("vitalType"));
                                String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                                String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));
                                String unitStr = StringHandler.defaultIfNull(document.get("unit"));
                                String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));

                                LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm

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
                        Log.d(TAG, "----------------------vitalsList count>> " + vitalsList.size()); // number of symptoms in the list

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

    public void GetVitalsByType2(String vitalType) {
        // Reference to the user's vitals collection
        CollectionReference vitalsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        // Create a query to find documents that match the specified criteria
        Query query = vitalsRef.whereEqualTo("vitalType", vitalType);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                vitalsList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Log.d(TAG, "----------------------Get Vitals By type--------------------------------");

                    // Retrieve the documents from the query result
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Log.d(TAG, "Document ID: " + document.getId());
                        Log.d(TAG, "vital Type: " + document.get("vitalType"));
                        Log.d(TAG, "unit: " + document.get("unit"));
                        Log.d(TAG, "measurement1: " + document.get("measurement1"));
                        Log.d(TAG, "measurement2: " + document.get("measurement2"));
                        Log.d(TAG, "record Time: " + document.get("recordTime"));
                        Log.d(TAG, "record Date: " + document.get("recordDate"));
                        Log.d(TAG, "--------------------------------------------");

                        try {
                            String vitalTypeStr = StringHandler.defaultIfNull(document.get("vitalType"));
                            String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                            String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));
                            String unitStr = StringHandler.defaultIfNull(document.get("unit"));
                            String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));
                            String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));

                            LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm
                            LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr); //convert from string format yyyy-MM-dd

                            // Create vital object with the retrieved data
                            Vital vital = new Vital(recordDate, recordTime, vitalTypeStr, unitStr);
                            vital.setVitalId(document.getId());
                            vital.setMeasurement1(measurement1);
                            vital.setMeasurement2(measurement2);

                            vitalsList.add(vital);
                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                        }
                    }
                    Log.d(TAG, "----------------------vitalsList count>> " + vitalsList.size()); // number of vitals in the list

                    searchResultMessageData.postValue("");
                    vitalsData.postValue(vitalsList);
                } else {
                    searchResultMessageData.postValue("No vitals found ...");
                    vitalsData.postValue(vitalsList);
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error retrieving documents: " + task.getException().getMessage());
                vitalsData.postValue(null);
            }
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
                .whereEqualTo("vitalType", vitalType)
                .orderBy("recordDate", Query.Direction.DESCENDING);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    vitalsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d(TAG, "----------------------Get Vitals By type--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "vital Type: " + document.get("vitalType"));
                            Log.d(TAG, "unit: " + document.get("unit"));
                            Log.d(TAG, "measurement1: " + document.get("measurement1"));
                            Log.d(TAG, "measurement2: " + document.get("measurement2"));
                            Log.d(TAG, "record Time: " + document.get("recordTime"));
                            Log.d(TAG, "record Date: " + document.get("recordDate"));
                            Log.d(TAG, "--------------------------------------------");

                            try {
                                String vitalTypeStr = StringHandler.defaultIfNull(document.get("vitalType"));
                                String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                                String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));
                                String unitStr = StringHandler.defaultIfNull(document.get("unit"));
                                String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));
                                String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));

                                LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm
                                LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr); //convert from string format yyyy-MM-dd

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
                        Log.d(TAG, "----------------------vitalsList count>> " + vitalsList.size()); // number of symptoms in the list

                        searchResultMessageData.postValue("");
                        vitalsData.postValue(vitalsList);
                    } else {
                        searchResultMessageData.postValue("No vitals found ...");
                        vitalsData.postValue(vitalsList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    vitalsData.postValue(null);// Handle failure
                });
    }

    public void GetVitalsByDateAndType(String searchDateStr, String vitalType) {

        //String searchDateStr = DateValidator.LocalDateToString(searchDate);
        LocalDate searchDate = DateValidator.StringToLocalDate(searchDateStr); //convert from string format yyyy-MM-dd

        // Reference to the user's vital collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        Query query;

        if (TextUtils.isEmpty(searchDateStr)){
            // Create a query to find documents that match the specified criteria
            query = symptomsRef
                    .whereEqualTo("vitalType", vitalType)
                    .orderBy("recordDate", Query.Direction.DESCENDING);
        }else{
            // Create a query to find documents that match the specified criteria
            query = symptomsRef
                    .whereEqualTo("recordDate", searchDateStr)
                    .whereEqualTo("vitalType", vitalType);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    vitalsList = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d(TAG, "----------------------Get vitals By Date & Type--------------------------------" );

                        // Retrieve the documents from the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "vital Type: " + document.get("vitalType"));
                            Log.d(TAG, "unit: " + document.get("unit"));
                            Log.d(TAG, "measurement1: " + document.get("measurement1"));
                            Log.d(TAG, "measurement2: " + document.get("measurement2"));
                            Log.d(TAG, "record Time: " + document.get("recordTime"));
                            Log.d(TAG, "record Date: " + document.get("recordDate"));
                            Log.d(TAG, "--------------------------------------------");

                            try {
                                String vitalTypeStr = StringHandler.defaultIfNull(document.get("vitalType"));
                                String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                                String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));
                                String unitStr = StringHandler.defaultIfNull(document.get("unit"));
                                String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));

                                LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm

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
                        Log.d(TAG, "----------------------vitalsList count>> " + vitalsList.size()); // number of symptoms in the list

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

    public CompletableFuture<Boolean> GetVitalById2(String vitalId) {
        CompletableFuture<Boolean> vitalRetrieved = new CompletableFuture<>();

        // Reference to the user's symptoms collection
        CollectionReference vitalsRef = db
                .collection("users")
                .document(uid)
                .collection("vitals");

        DocumentReference docRef = vitalsRef.document(vitalId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        selectedVital = new Vital();

                        // Document exists, retrieve the data
                        Map<String, Object> document = documentSnapshot.getData();

                        Log.d(TAG, "Document ID: " + vitalId);
                        Log.d(TAG, "recordDate: " + document.get("recordDate"));
                        Log.d(TAG, "recordTime: " + document.get("recordTime"));
                        Log.d(TAG, "vitalType: " + document.get("vitalType"));
                        Log.d(TAG, "measurement1: " + document.get("measurement1"));
                        Log.d(TAG, "measurement2: " + document.get("measurement2"));
                        Log.d(TAG, "unit: " + document.get("unit"));
                        Log.d(TAG, "--------------------------------------------");

                        try {
                            String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));
                            String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));
                            String vitalTypeStr = StringHandler.defaultIfNull(document.get("vitalType"));
                            String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                            String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));
                            String unitStr = StringHandler.defaultIfNull(document.get("unit"));

                            LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm
                            LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr); //convert from string format yyyy-MM-dd

                            //create vital object with the retrieved data
                            selectedVital = new Vital(recordDate, recordTime, vitalTypeStr,unitStr);
                            selectedVital.setVitalId(vitalId);
                            selectedVital.setMeasurement1(measurement1);
                            selectedVital.setMeasurement2(measurement2);

                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                            vitalRetrieved.complete(false);
                        }

                        searchResultMessageData.postValue("");
                        selectedVitalData.postValue(selectedVital);
                        vitalRetrieved.complete(true);
                    } else {
                        // Document does not exist
                        searchResultMessageData.postValue("vital is not found");
                        selectedVitalData.postValue(null);
                        Log.e("Error", "No such document");
                        vitalRetrieved.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Error", "Error getting document: " + e.getMessage());
                    selectedVitalData.postValue(null);
                    vitalRetrieved.complete(false);
                });
        return vitalRetrieved;
    }

    public CompletableFuture<Boolean> GetVitalById(String vitalId) {
        CompletableFuture<Boolean> vitalRetrieved = new CompletableFuture<>();
        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = db
                .collection("users")
                .document(uid)
                .collection("vitals");

        Log.d(TAG, "vital ID: " + vitalId);

        DocumentReference docRef = symptomsRef.document(vitalId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        selectedVital = new Vital();

                        // Document exists, retrieve the data
                        Map<String, Object> document = documentSnapshot.getData();
                        try {
                            Log.d(TAG, "Document ID: " + vitalId);
                            Log.d(TAG, "recordDate: " + document.get("recordDate"));
                            Log.d(TAG, "recordTime: " + document.get("recordTime"));
                            Log.d(TAG, "vitalType: " + document.get("vitalType"));
                            Log.d(TAG, "measurement1: " + document.get("measurement1"));
                            Log.d(TAG, "measurement2: " + document.get("measurement2"));
                            Log.d(TAG, "unit: " + document.get("unit"));
                            Log.d(TAG, "--------------------------------------------");

                            String vitalType = StringHandler.defaultIfNull(document.get("vitalType").toString());
                            String unit = StringHandler.defaultIfNull(document.get("unit"));
                            String recordDateStr = StringHandler.defaultIfNull(document.get("recordDate"));
                            String recordTimeStr = StringHandler.defaultIfNull(document.get("recordTime"));
                            String measurement1 = StringHandler.defaultIfNull(document.get("measurement1"));
                            String measurement2 = StringHandler.defaultIfNull(document.get("measurement2"));


                            if (DateValidator.isValidDate(recordDateStr) && TimeValidator.isValidTime(recordTimeStr)){
                                LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);//convert from string format HH:mm
                                LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr); //convert from string format yyyy-MM-dd

                                //create symptom object with the retrieved data
                                selectedVital = new Vital(recordDate,recordTime,vitalType,unit);
                                selectedVital.setVitalId(vitalId);
                                selectedVital.setMeasurement1(measurement1);
                                selectedVital.setMeasurement2(measurement2);
                            }else{
                                Log.e("Error", "Date or Time is not valid" );
                                Log.d(TAG, "Date or Time is not valid" );

                            }

                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                            Log.d(TAG, "Error parsing time: " + e.getMessage());
                            vitalRetrieved.complete(false);
                        }

                        searchResultMessageData.postValue("");
                        selectedVitalData.postValue(selectedVital);
                        vitalRetrieved.complete(true);
                    } else {
                        // Document does not exist
                        searchResultMessageData.postValue("vital is not found");
                        selectedVitalData.postValue(null);
                        Log.e("Error", "No such document");
                        Log.d(TAG, "Error: No such document");
                        vitalRetrieved.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Error", "Error getting document: " + e.getMessage());
                    Log.d(TAG, "Error getting document: " + e.getMessage());
                    selectedVitalData.postValue(null);
                    vitalRetrieved.complete(false);
                });
        return vitalRetrieved;
    }

    public void UpdateVital(Vital updatedVital) {

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals");

        // Document reference for the specific symptom to be updated
        DocumentReference docRef = symptomsRef.document(updatedVital.getVitalId());

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("recordDate", DateValidator.LocalDateToString(updatedVital.getRecordDate()));
        updatedData.put("recordTime", TimeValidator.LocalTimeToString(updatedVital.getRecordTime()));
        updatedData.put("vitalType", updatedVital.getVitalType());
        updatedData.put("measurement1", updatedVital.getMeasurement1());
        updatedData.put("measurement2", updatedVital.getMeasurement2());
        updatedData.put("unit",updatedVital.getUnit());

        // Update the document in Firestore
        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Vital updated successfully.");
                    searchResultMessageData.postValue("Vital is updated successfully.");
                    selectedVital =updatedVital;
                    selectedVitalData.postValue(updatedVital);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error updating vital: " + e.getMessage());
                    searchResultMessageData.postValue("Error updating vital");
                });
    }

    public void DeleteVital(String vitalId) {
        // Reference to the specific symptom document
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vitals")
                .document(vitalId);

        // Delete the document
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "vital record is deleted successfully.");
                    //searchResultMessageData.postValue("vital record is deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error deleting vital: " + e.getMessage());
                    //searchResultMessageData.postValue("Error deleting vital.");
                });
    }

}
