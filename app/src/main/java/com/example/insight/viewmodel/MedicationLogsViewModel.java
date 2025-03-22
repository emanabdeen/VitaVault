package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.MedicationLog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MedicationLogsViewModel extends ViewModel {

    private final MutableLiveData<List<MedicationLog>> logsLiveData = new MutableLiveData<>();

    public LiveData<List<MedicationLog>> getLogs() {
        return logsLiveData;
    }

    public void fetchMedicationLogs(String medicationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("medications").document(medicationId)
                .collection("logs")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MedicationLog> logs = new ArrayList<>();
                    Log.d("MedicationLogsVM", "Logs fetched: " + queryDocumentSnapshots.size());

                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        String status = doc.getString("status");
                        Timestamp timestamp = doc.getTimestamp("timestamp");
                        String time = (timestamp != null) ? timestamp.toDate().toString() : "Unknown";
                        String dosageWithUnit = doc.getString("dosage"); // Already stored in log document

                        logs.add(new MedicationLog(dosageWithUnit, status, time));
                    }
                    logsLiveData.setValue(logs);
                })
                .addOnFailureListener(e -> Log.e("MedicationLogsVM", "Failed to fetch logs", e));
    }
}
