package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.MedicationLog;
import com.example.insight.utility.MedicationLogUtils;
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
                        String logId = doc.getId();
                        String status = doc.getString("status");
                        Timestamp timestamp = doc.getTimestamp("timestamp");
                        String time = (timestamp != null) ? timestamp.toDate().toString() : "Unknown";
                        String dosageWithUnit = doc.getString("dosage"); // Already stored in log document

                        logs.add(new MedicationLog(logId, dosageWithUnit, status, time));
                    }
                    logsLiveData.setValue(logs);
                })
                .addOnFailureListener(e -> Log.e("MedicationLogsVM", "Failed to fetch logs", e));
    }

    public void deleteLog(String medicationId, String logId, MedicationLogUtils.MedicationLogCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("medications")
                .document(medicationId)
                .collection("logs")
                .document(logId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Logs", "✅ Deleted log: " + logId);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Logs", "❌ Failed to delete log", e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }
}
