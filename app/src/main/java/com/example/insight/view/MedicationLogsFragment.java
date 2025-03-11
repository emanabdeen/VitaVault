package com.example.insight.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.MedicationLogAdapter;
import com.example.insight.databinding.FragmentMedicationLogsBinding;
import com.example.insight.model.MedicationLog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MedicationLogsFragment extends Fragment {

    private String medicationId;
    private FragmentMedicationLogsBinding binding;
    private MedicationLogAdapter adapter;

    public static MedicationLogsFragment newInstance(String medicationId) {
        MedicationLogsFragment fragment = new MedicationLogsFragment();
        Bundle args = new Bundle();
        args.putString("medicationID", medicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicationId = getArguments().getString("medicationID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicationLogsBinding.inflate(inflater, container, false);
        setupRecyclerView();
        fetchMedicationLogs(medicationId);
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new MedicationLogAdapter(requireContext(), new ArrayList<>());
        binding.recyclerViewLogs.setAdapter(adapter);
        binding.recyclerViewLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void fetchMedicationLogs(String medicationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 🔑 First fetch medication details to get dosage & unit
        db.collection("users").document(userId)
                .collection("medications").document(medicationId)
                .get().addOnSuccessListener(medDoc -> {
                    String dosage = medDoc.getString("dosage");
                    String unit = medDoc.getString("unit");

                    // Debug log
                    Log.d("MedicationLogsActivity", "Fetched dosage: " + dosage + ", unit: " + unit);

                    // ✅ Now fetch logs for this medication
                    db.collection("users").document(userId)
                            .collection("medications").document(medicationId)
                            .collection("logs")
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                List<MedicationLog> logs = new ArrayList<>();
                                Log.d("MedicationLogsActivity", "Logs fetched: " + queryDocumentSnapshots.size());

                                for (var doc : queryDocumentSnapshots.getDocuments()) {
                                    Log.d("MedicationLogsActivity", "Log Data: " + doc.getData());

                                    String status = doc.getString("status");
                                    Timestamp timestamp = doc.getTimestamp("timestamp");
                                    String time = timestamp != null ? timestamp.toDate().toString() : "Unknown";

                                    // Combine dosage and unit nicely
                                    String dosageWithUnit = (dosage != null && unit != null) ? dosage + " " + unit : "Unknown";

                                    // Build MedicationLog object
                                    logs.add(new MedicationLog(dosageWithUnit, status, time));
                                }

                                // ✅ Update RecyclerView or show empty message
                                if (logs.isEmpty()) {
                                    binding.textViewNoLogs.setVisibility(View.VISIBLE);
                                    binding.recyclerViewLogs.setVisibility(View.GONE);
                                } else {
                                    binding.textViewNoLogs.setVisibility(View.GONE);
                                    binding.recyclerViewLogs.setVisibility(View.VISIBLE);
                                    adapter.updateLogs(logs);
                                }
                            })
                            .addOnFailureListener(e -> Log.e("MedicationLogsActivity", "Failed to fetch logs", e));
                })
                .addOnFailureListener(e -> Log.e("MedicationLogsActivity", "Failed to fetch medication details", e));
    }


}

