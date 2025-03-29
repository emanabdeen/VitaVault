package com.example.insight.utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.insight.receiver.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MedicationLogUtils {

    public interface MedicationLogCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void logMedicationStatus(Context context,
                                           String medicationId,
                                           String dosage,
                                           String status,
                                           @Nullable Date timestamp,
                                           boolean stopAlarmAfterLog,
                                           MedicationLogCallback callback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        // Generate document reference with unique ID
        var logRef = db.collection("users").document(uid)
                .collection("medications").document(medicationId)
                .collection("logs").document();

        String logId = logRef.getId();

        Map<String, Object> log = new HashMap<>();
        log.put("logId", logId);  // ⬅ Include the ID in the document
        log.put("medicationId", medicationId);
        log.put("status", status);
        log.put("dosage", dosage);

        if (timestamp != null) {
            log.put("timestamp", timestamp);
        } else {
            log.put("timestamp", FieldValue.serverTimestamp());
        }

        logRef.set(log)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MedicationLogUtils", "✅ Logged as " + status + " with ID: " + logId);

                    if (stopAlarmAfterLog) {
                        AlarmReceiver.stopAlarm(context);
                    }

                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("MedicationLogUtils", "❌ Failed to log", e);
                    callback.onFailure(e);
                });
    }

    public static void updateMedicationLog(
            Context context,
            String medicationId,
            String logId,
            String newStatus,
            Date newTimestamp,
            MedicationLogCallback callback
    ) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference logRef = db.collection("users")
                .document(userId)
                .collection("medications")
                .document(medicationId)
                .collection("logs")
                .document(logId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("timestamp", newTimestamp);

        logRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MedicationLogUtils", "✅ Log updated: " + logId);
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("MedicationLogUtils", "❌ Failed to update log", e);
                    if (callback != null) callback.onFailure(e);
                });
    }


}
