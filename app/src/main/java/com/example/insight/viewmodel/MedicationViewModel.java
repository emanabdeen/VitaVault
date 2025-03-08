package com.example.insight.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.insight.model.Medication;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicationViewModel extends ViewModel {
    private static final String TAG = "MedicationViewModel";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = auth.getCurrentUser();
    private final String uid = currentUser != null ? currentUser.getUid() : null;

    private final MutableLiveData<List<Medication>> medicationsData = new MutableLiveData<>();
    private List<Medication> medicationsList = new ArrayList<>();

    public MedicationViewModel() {
        medicationsData.setValue(medicationsList);
    }

    public LiveData<List<Medication>> getMedicationsData() {
        return medicationsData;
    }

    /**
     * ✅ Add a Medication to Firestore
     */
    public void addMedication(Medication medication) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        // Reference to user's medications collection
        CollectionReference medicationsRef = db.collection("users").document(uid).collection("medications");

        // Generate unique ID for medication
        String medicationId = medicationsRef.document().getId();
        medication.setMedicationId(medicationId);

        // Prepare data to save
        Map<String, Object> medicationData = new HashMap<>();
        medicationData.put("medicationId", medicationId);
        medicationData.put("name", medication.getName());
        medicationData.put("dosage", medication.getDosage());
        medicationData.put("unit", medication.getUnit());
        medicationData.put("reminderTime", medication.getReminderMap()); // Default value
        medicationData.put("reminderEnabled", medication.isReminderEnabled());


        // Save to Firestore
        medicationsRef.document(medicationId).set(medicationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medication added successfully: " + medicationId);
                    getMedications();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding medication", e);
                });
    }

    /**
     * ✅ Retrieve Medications for the Current User
     */
    public void getMedications() {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        medicationsData.postValue(null);
        db.collection("users").document(uid).collection("medications")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    medicationsList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Medication medication = document.toObject(Medication.class);
                        if (medication != null) {
                            medicationsList.add(medication);
                        }
                    }
                    medicationsData.postValue(medicationsList);
                    medicationsData.setValue(medicationsList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving medications", e);
                }).addOnCompleteListener(task -> {
                    // ✅ Hide progress bar when data fetching is done
                    medicationsData.postValue(medicationsList);
                });;
    }

    public void updateMedication(Medication medication) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        DocumentReference medicationRef = db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medication.getMedicationId());

        medicationRef.set(medication)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Medication updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating medication", e));
    }


    /**
     * ✅ Log When a Medication is Taken
     */
    public void logMedicationTaken(String medicationId) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        DocumentReference medicationRef = db.collection("users").document(uid).collection("medications").document(medicationId);
        CollectionReference logsRef = medicationRef.collection("medication_logs");

        String logId = logsRef.document().getId();
        Map<String, Object> logData = new HashMap<>();
        logData.put("logId", logId);
        logData.put("takenAt", LocalDate.now().format(DateTimeFormatter.ISO_DATE) + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        logData.put("confirmation", true);

        logsRef.document(logId).set(logData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Medication log added"))
                .addOnFailureListener(e -> Log.e(TAG, "Error logging medication", e));
    }

    /**
     * ✅ Delete a Medication
     */
    public void removeMedication(String medicationId) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        db.collection("users").document(uid).collection("medications").document(medicationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medication deleted: " + medicationId);
                        getMedications();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting medication", e));
    }
}
