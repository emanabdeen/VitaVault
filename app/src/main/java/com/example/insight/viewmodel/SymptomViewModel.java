package com.example.insight.viewmodel;

import android.util.Log;

import com.example.insight.model.CoughingSymptom;
import com.example.insight.model.NauseaSymptom;
import com.example.insight.model.OtherSymptom;
import com.example.insight.model.Symptom;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import kotlinx.coroutines.channels.ActorKt;


public class SymptomViewModel {
    //inst the FirebaseFirestore (DB)
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //constructor
    public SymptomViewModel() {

    }

    //Add symptom
    public void AddSymptomToFav( String uid, Symptom symptom){

        // Ensure `recordDate` and `startTime` are properly formatted
        String recordDateStr = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String startTimeStr = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeStr = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        // Create an ID for the symptoms document based on the date
        String symptID = "symp" + symptom.getRecordDate().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        // Create a map to hold symptom details
        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("recordDate", recordDateStr);
        symptomData.put("startTime", startTimeStr);
        symptomData.put("endTime", endTimeStr);


        DocumentReference symptomRef = db.collection("users")
                .document(uid)
                .collection("symptoms")
                .document(symptID); // Default reference (if no match)



        // Check the type of the symptom and add subclass-specific properties
        if (symptom instanceof CoughingSymptom) {
            String coughID = "cough" + symptom.getStartTime().format(DateTimeFormatter.ofPattern("HHmm"));
            symptomData.put("coughLevel", ((CoughingSymptom) symptom).getCoughLevel());

            symptomRef = symptomRef.collection("coughingSymptom").document(coughID);

        } else if (symptom instanceof NauseaSymptom) {
            String nauseaID = "nausea" + symptom.getStartTime().format(DateTimeFormatter.ofPattern("HHmm"));
            symptomData.put("nauseaLevel", ((NauseaSymptom) symptom).getNauseaLevel());

            symptomRef = symptomRef.collection("nauseaSymptom").document(nauseaID);

        } else if (symptom instanceof OtherSymptom) {
            String otherSymptomID = "other" + symptom.getStartTime().format(DateTimeFormatter.ofPattern("HHmm"));
            symptomData.put("symptomDescription", ((OtherSymptom) symptom).getDescription());

            symptomRef = symptomRef.collection("otherSymptom").document(otherSymptomID);
        }

        // Save the symptom data to Firestore
        symptomRef.set(symptomData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Added Successfully ✔"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add: " + e.getMessage()));

    }


}
