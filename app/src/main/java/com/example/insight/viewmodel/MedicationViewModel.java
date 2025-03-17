package com.example.insight.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.Medication;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.utility.AlarmLocalStorageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MedicationViewModel extends ViewModel {

    private static final String TAG = "MedicationViewModel";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = auth.getCurrentUser();
    private final String uid = (currentUser != null) ? currentUser.getUid() : null;

    // Observables
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<Medication>> medicationsData = new MutableLiveData<>();
    private final MutableLiveData<Medication> medicationToDelete = new MutableLiveData<>();
    private final MutableLiveData<Medication> medicationLiveData = new MutableLiveData<>();

    private List<Medication> medicationsList = new ArrayList<>();

    public MedicationViewModel() {
        medicationsData.setValue(medicationsList);
    }

    public LiveData<List<Medication>> getMedicationsData() {
        return medicationsData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Medication> getMedicationToDelete() {
        return medicationToDelete;
    }

    public LiveData<Medication> getMedication(String medicationId) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return medicationLiveData;
        }
        db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Medication medication = documentSnapshot.toObject(Medication.class);
                    medicationLiveData.postValue(medication);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch medication", e));

        return medicationLiveData;
    }

    // ------------------------------------------------------------------------
    // Add a new Medication document to Firestore (basic info only)
    // ------------------------------------------------------------------------
    public void addMedication(Medication medication) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        // Reference to user's "medications" collection
        CollectionReference medicationsRef = db.collection("users")
                .document(uid)
                .collection("medications");

        // Generate unique ID for the medication
        String medicationId = medicationsRef.document().getId();
        medication.setMedicationId(medicationId);

        // Save the medication's basic fields
        medicationsRef.document(medicationId).set(medication)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medication added successfully: " + medicationId);
                    // Optionally refresh the list
                    getMedications(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding medication", e);
                });
    }

    // ------------------------------------------------------------------------
    // Retrieve all Medications for the current user
    // ------------------------------------------------------------------------
    public void getMedications(boolean showLoading) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        if (showLoading) {
            isLoading.postValue(true);
        }

        db.collection("users")
                .document(uid)
                .collection("medications")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    medicationsList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Medication med = document.toObject(Medication.class);
                        if (med != null) {
                            medicationsList.add(med);
                        }
                    }
                    medicationsData.postValue(medicationsList);
                    isLoading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving medications", e);
                    isLoading.postValue(false);
                })
                .addOnCompleteListener(task -> {
                    // Hide progress bar when done
                    isLoading.postValue(false);
                });
    }

    // ------------------------------------------------------------------------
    // Update an existing Medication document
    // ------------------------------------------------------------------------
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
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medication updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating medication", e);
                });
    }

    // ------------------------------------------------------------------------
    // Prepare to remove a Medication (set up for a confirmation flow, etc.)
    // ------------------------------------------------------------------------
    public void prepareToRemoveMedication(Medication medication) {
        medicationToDelete.setValue(medication);
    }

    // ------------------------------------------------------------------------
    // Delete a Medication and all its logs
    // ------------------------------------------------------------------------
    public void removeMedication(String medicationId) {
        if (uid == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        DocumentReference medicationDoc = db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId);

        // Fetch and delete all log and alarm documents and then medication
        medicationDoc.collection("logs")
                .get()
                .addOnSuccessListener(logSnapshot -> {
                    // Then fetch alarms subcollection
                    medicationDoc.collection("alarms")
                            .get()
                            .addOnSuccessListener(alarmSnapshot -> {
                                WriteBatch batch = db.batch();

                                // Delete all log documents
                                logSnapshot.getDocuments().forEach(doc -> batch.delete(doc.getReference()));

                                // Delete all alarm documents
                                alarmSnapshot.getDocuments().forEach(doc -> batch.delete(doc.getReference()));

                                // Commit batch deletion for logs and alarms
                                batch.commit().addOnSuccessListener(aVoid -> {
                                    // Now delete the medication document itself
                                    medicationDoc.delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d(TAG, "Medication, its logs, and alarms deleted: " + medicationId);
                                                getMedications(false);
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG, "Error deleting medication", e));
                                }).addOnFailureListener(e -> Log.e(TAG, "Error deleting logs/alarms", e));
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error retrieving alarms", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error retrieving logs", e));
    }

    public void deleteMedicationAndAlarms(Medication medication, Context appContext) {
        // 1. Delete the medication from Firestore.
        removeMedication(medication.getMedicationId());

        // 2. Retrieve locally stored alarms.
        List<MedicationAlarm> localAlarms = AlarmLocalStorageHelper.getAlarms(appContext);

        // 3. Loop through and cancel/remove alarms associated with this medication.
        for (MedicationAlarm alarm : localAlarms) {
            if (alarm.getMedicationId().equals(medication.getMedicationId())) {
                int requestCode = (alarm.getMedicationId() + alarm.getDay() + alarm.getTime()).hashCode();
                AlarmHelper.cancelAlarm(appContext, requestCode, alarm.getMedicationId(), alarm.getMedicationName());
                AlarmLocalStorageHelper.removeAlarm(appContext, alarm);
            }
        }
    }


}
