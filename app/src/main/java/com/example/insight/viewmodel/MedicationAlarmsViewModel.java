package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.MedicationAlarm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MedicationAlarmsViewModel extends ViewModel {

    private final MutableLiveData<List<MedicationAlarm>> alarms = new MutableLiveData<>();

    public LiveData<List<MedicationAlarm>> getAlarms() {
        return alarms;
    }

    public void fetchAlarms(String medicationId) {
        // Get Firestore instance and current user ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated
            return;
        }
        String uid = currentUser.getUid();

        // Reference the alarms subcollection within the medication document
        CollectionReference alarmsRef = db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .collection("alarms");

        alarmsRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MedicationAlarm> fetchedAlarms = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        MedicationAlarm alarm = doc.toObject(MedicationAlarm.class);
                        if (alarm != null) {
                            fetchedAlarms.add(alarm);
                        }
                    }
                    Log.d("MedicationAlarmsVM", "Fetched alarms: " + fetchedAlarms.size());
                    // Post the list of alarms to LiveData
                    alarms.postValue(fetchedAlarms);
                })
                .addOnFailureListener(e -> {
                    // Log or handle the error as needed
                    Log.e("MedicationAlarmsVM", "Error fetching alarms", e);
                });
    }
    public void addAlarm(String medicationId, MedicationAlarm alarm) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User not authenticated
            return;
        }
        String uid = user.getUid();

        // Reference Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Point to "users/{uid}/medications/{medicationId}/alarms"
        CollectionReference alarmsRef = db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .collection("alarms");

        // Generate a new doc reference for this alarm
        DocumentReference alarmDocRef = alarmsRef.document();
        String alarmId = alarmDocRef.getId();

        // Store the doc ID in the alarm object
        alarm.setAlarmId(alarmId);
        alarm.setMedicationId(medicationId);

        // Save the alarm document
        alarmDocRef.set(alarm)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MedicationAlarmsVM", "Alarm added successfully: " + alarmId);
                    // Optionally refresh or fetch alarms again
                    // fetchAlarms(medicationId);
                })
                .addOnFailureListener(e -> {
                    Log.e("MedicationAlarmsVM", "Error adding alarm", e);
                });
    }

    public void updateAlarm(String medicationId, MedicationAlarm alarm) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ensure the alarm has a valid alarmId to update
        if (alarm.getAlarmId() == null) {
            Log.e("MedicationAlarmsVM", "Cannot update alarm: alarmId is null");
            return;
        }

        DocumentReference alarmDocRef = db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .collection("alarms")
                .document(alarm.getAlarmId());

        alarmDocRef.set(alarm)
                .addOnSuccessListener(aVoid -> Log.d("MedicationAlarmsVM", "Alarm updated successfully: " + alarm.getAlarmId()))
                .addOnFailureListener(e -> Log.e("MedicationAlarmsVM", "Error updating alarm", e));
    }


    public interface OnAlarmsFetchedCallback {
        void onAlarmsFetched(List<MedicationAlarm> alarmList);
    }

    /**
     * Fetch all alarms for a given medication, then pass them to the callback.
     */
    public void fetchAlarmsForDeletion(String medicationId, OnAlarmsFetchedCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // No user, no fetch
            callback.onAlarmsFetched(new ArrayList<>());
            return;
        }

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .collection("alarms")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MedicationAlarm> alarmList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        MedicationAlarm alarm = doc.toObject(MedicationAlarm.class);
                        if (alarm != null) {
                            alarmList.add(alarm);
                        }
                    }
                    callback.onAlarmsFetched(alarmList);
                })
                .addOnFailureListener(e -> {
                    // In case of error, just return an empty list
                    callback.onAlarmsFetched(new ArrayList<>());
                });
    }

    public void deleteAlarm(String medicationId, String alarmId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(uid)
                .collection("medications")
                .document(medicationId)
                .collection("alarms")
                .document(alarmId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("MedicationAlarmsVM", "Alarm deleted: " + alarmId))
                .addOnFailureListener(e -> Log.e("MedicationAlarmsVM", "Error deleting alarm", e));
    }

}
