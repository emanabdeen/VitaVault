package com.example.insight.viewmodel;

import android.util.Log;

import com.example.insight.model.Symptom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SymptomViewModel {
    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //constructor
    public SymptomViewModel() { }


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

    public void GetSymptomsByDate(String uid, LocalDate searchDate, OnSymptomsListRetrievedListener listener) {

        // Extract search date details
        int dayOfMonth = searchDate.getDayOfMonth();
        int monthValue = searchDate.getMonthValue();
        int year = searchDate.getYear();
        String searchDateStr = searchDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("recordDate", searchDateStr);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Symptom> symptoms = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
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

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptoms.add(symptom);
                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        listener.onSymptomsRetrieved(symptoms);
                    } else {
                        listener.onSymptomsRetrieved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    listener.onSymptomsRetrieved(null);  // Handle failure
                });

    }

    public void GetSymptomsByDateAndType(String uid, LocalDate searchDate, String symptomType,OnSymptomsListRetrievedListener listener) {

        String searchDateStr = searchDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("recordDate", searchDateStr)
                .whereEqualTo("symptomName", symptomType);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Symptom> symptoms = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
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

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(searchDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptoms.add(symptom);
                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        listener.onSymptomsRetrieved(symptoms);
                    } else {
                        listener.onSymptomsRetrieved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    listener.onSymptomsRetrieved(null);  // Handle failure
                });

    }

    public void GetSymptomsByDateRange(String uid, LocalDate date1, LocalDate date2, OnSymptomsListRetrievedListener listener) {

        // Format LocalDate to String for comparison (same format as used in AddSymptom method)
        String startDateStr = date1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateStr = date2.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereGreaterThanOrEqualTo("recordDate", startDateStr)
                .whereLessThanOrEqualTo("recordDate", endDateStr);


        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Symptom> symptoms = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
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
                                String recordDateStr = document.getString("recordDate");

                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptoms.add(symptom);

                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        listener.onSymptomsRetrieved(symptoms);
                    } else {
                        listener.onSymptomsRetrieved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    listener.onSymptomsRetrieved(null);  // Handle failure
                });

//        // Execute the query using Firebase Task API
//        Task<QuerySnapshot> task = query.get();
//
//        // Add an OnCompleteListener to handle the task result
//        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot querySnapshot = task.getResult();
//                    List<Symptom> symptoms = new ArrayList<>();
//                    if (querySnapshot != null) {
//                        // Retrieve the documents from the query result
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            Log.d("debug", "Document ID: " + document.getId());
//                            Log.d("debug", "Symptom Name: " + document.getString("symptomName"));
//                            Log.d("debug", "Symptom Level: " + document.getString("symptomLevel"));
//                            Log.d("debug", "Record Date: " + document.getString("recordDate"));
//                            Log.d("debug", "Start Time: " + document.getString("startTime"));
//                            Log.d("debug", "End Time: " + document.getString("endTime"));
//                            Log.d("debug", "Description: " + document.getString("symptomDescription"));
//                            Log.d("debug", "--------------------------------------------");
//
//                            try {
//                                String symptomName = document.getString("symptomName");
//                                String symptomLevel = document.getString("symptomLevel");
//                                String symptomDescription = document.getString("symptomDescription");
//                                String startTimeStr = document.getString("startTime");
//                                String endTimeStr = document.getString("endTime");
//                                String recordDateStr = document.getString("recordDate");
//
//                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
//                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
//                                LocalDate recordDate =LocalDate.parse(recordDateStr,DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//
//                                //create symptom object with the retrieved data
//                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
//                                symptom.setSymptomId(document.getId());
//
//                                symptoms.add(symptom);
//
//                            } catch (DateTimeParseException e) {
//                                Log.e("Error", "Error parsing time: " + e.getMessage());
//                            }
//                        }
//                        listener.onSymptomsRetrieved(symptoms);
//                    }else {
//                        listener.onSymptomsRetrieved(null); // Or an empty list if you prefer
//                    }
//                } else {
//                    System.err.println("Error retrieving documents: " + task.getException().getMessage());
//                }
//            }
//        });
    }

    public void GetSymptomsByType(String uid, String symptomType, OnSymptomsListRetrievedListener listener) {


        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Create a query to find documents that match the specified criteria
        Query query = symptomsRef
                .whereEqualTo("symptomName", symptomType);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Symptom> symptoms = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
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
                                String recordDateStr = document.getString("recordDate");

                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                // Create symptom object with the retrieved data
                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                                symptom.setSymptomId(document.getId());

                                symptoms.add(symptom);

                            } catch (DateTimeParseException e) {
                                Log.e("Error", "Error parsing time: " + e.getMessage());
                            }
                        }
                        listener.onSymptomsRetrieved(symptoms);
                    } else {
                        listener.onSymptomsRetrieved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error retrieving documents: " + e.getMessage());
                    listener.onSymptomsRetrieved(null);  // Handle failure
                });

//        // Execute the query using Firebase Task API
//        Task<QuerySnapshot> task = query.get();
//
//        // Add an OnCompleteListener to handle the task result
//        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot querySnapshot = task.getResult();
//                    List<Symptom> symptoms = new ArrayList<>();
//                    if (querySnapshot != null) {
//                        // Retrieve the documents from the query result
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            Log.d("debug", "Document ID: " + document.getId());
//                            Log.d("debug", "Symptom Name: " + document.getString("symptomName"));
//                            Log.d("debug", "Symptom Level: " + document.getString("symptomLevel"));
//                            Log.d("debug", "Record Date: " + document.getString("recordDate"));
//                            Log.d("debug", "Start Time: " + document.getString("startTime"));
//                            Log.d("debug", "End Time: " + document.getString("endTime"));
//                            Log.d("debug", "Description: " + document.getString("symptomDescription"));
//                            Log.d("debug", "--------------------------------------------");
//
//                            try {
//                                String symptomName = document.getString("symptomName");
//                                String symptomLevel = document.getString("symptomLevel");
//                                String symptomDescription = document.getString("symptomDescription");
//                                String startTimeStr = document.getString("startTime");
//                                String endTimeStr = document.getString("endTime");
//                                String recordDateStr = document.getString("recordDate");
//
//                                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
//                                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
//                                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//
//                                //create symptom object with the retrieved data
//                                Symptom symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
//                                symptom.setSymptomId(document.getId());
//
//                                symptoms.add(symptom);
//
//                            } catch (DateTimeParseException e) {
//                                Log.e("Error", "Error parsing time: " + e.getMessage());
//                            }
//                        }
//                        listener.onSymptomsRetrieved(symptoms);
//                    }else {
//                        listener.onSymptomsRetrieved(null); // Or an empty list if you prefer
//                    }
//                } else {
//                    System.err.println("Error retrieving documents: " + task.getException().getMessage());
//                }
//            }
//        });
    }

    public void GetSymptomById(String uid, String symptomId, OnSymptomObjectRetrievedListener listener) {

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");


        DocumentReference docRef = symptomsRef.document(symptomId);

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Symptom symptom = new Symptom();

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
                            String symptomName = document.get("symptomName").toString();
                            String symptomLevel = document.get("symptomLevel").toString();
                            String symptomDescription = document.get("symptomDescription").toString();
                            String startTimeStr = document.get("startTime").toString();
                            String endTimeStr = document.get("endTime").toString();
                            String recordDateStr = document.get("recordDate").toString();

                            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                            LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                            LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                            //create symptom object with the retrieved data
                            symptom = new Symptom(recordDate, startTime, endTime, symptomName, symptomLevel, symptomDescription);
                            symptom.setSymptomId(symptomId);

                        } catch (DateTimeParseException e) {
                            Log.e("Error", "Error parsing time: " + e.getMessage());
                        }

                        listener.onSymptomRetrieved(symptom);

                    } else {
                        // Document does not exist
                        listener.onSymptomRetrieved(null);
                        Log.e("Error", "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Error", "Error getting document: " + e.getMessage());
                });
    }

    public void UpdateSymptom(String uid, String symptomId, Symptom symptom) {

        // Reference to the user's symptoms collection
        CollectionReference symptomsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("symptoms");

        // Document reference for the specific symptom to be updated
        DocumentReference docRef = symptomsRef.document(symptomId);


        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("symptomName", symptom.getSymptomName());
        updatedData.put("symptomLevel", symptom.getSymptomLevel());
        updatedData.put("symptomDescription", symptom.getSymptomDescription());
        updatedData.put("startTime", symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        updatedData.put("endTime", symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        updatedData.put("recordDate", symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Update the document in Firestore
        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("debug", "Symptom updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error updating symptom: " + e.getMessage());
                });
    }



    // Interface for callback when symptoms list are retrieved
    public interface OnSymptomsListRetrievedListener {
        void onSymptomsRetrieved(List<Symptom> symptoms);
    }

    // Interface for callback when symptoms list are retrieved
    public interface OnSymptomObjectRetrievedListener {
        void onSymptomRetrieved(Symptom symptom);
    }


}
