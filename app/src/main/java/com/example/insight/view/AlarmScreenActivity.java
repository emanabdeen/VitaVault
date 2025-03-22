package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityAlarmScreenBinding;
import com.example.insight.receiver.AlarmReceiver;
import com.example.insight.utility.AlarmSoundHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AlarmScreenActivity extends AppCompatActivity {

    private ActivityAlarmScreenBinding binding;
    private String medicationId, medicationName, dosage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the view from binding

        // Get passed data
        medicationId = getIntent().getStringExtra("medicationId");
        medicationName = getIntent().getStringExtra("medicationName");
        dosage = getIntent().getStringExtra("dosage");

        // Set medication name
        binding.txtMedicationName.setText(medicationName);
        binding.txtMedicationDosage.setText(dosage);

        // Set button click listeners
        binding.btnTaken.setOnClickListener(v -> logMedication("Taken"));
        binding.btnMissed.setOnClickListener(v -> logMedication("Missed"));

        binding.btnStopSound.setOnClickListener(v -> {
            AlarmSoundHelper.stopAlarmSound();
        });
    }

    // Method to log medication as Taken or Missed
    private void logMedication(String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> log = new HashMap<>();
        log.put("medicationId", medicationId);
        log.put("status", status);
        log.put("timestamp", FieldValue.serverTimestamp());
        log.put("dosage", dosage);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("medications")
                .document(medicationId).collection("logs").add(log)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Logged as: " + status, Toast.LENGTH_SHORT).show();

                    //stop alarm
                    AlarmReceiver.stopAlarm(this);

                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to log", Toast.LENGTH_SHORT).show();
                });
    }

    // Optionally prevent back button to force choice
    @Override
    public void onBackPressed() {
        // Disabled to ensure user makes a choice
    }
}
