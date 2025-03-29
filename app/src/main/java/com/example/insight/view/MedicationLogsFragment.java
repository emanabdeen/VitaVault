package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.MedicationLogAdapter;
import com.example.insight.databinding.FragmentMedicationLogsBinding;
import com.example.insight.model.MedicationLog;
import com.example.insight.utility.MedicationLogUtils;
import com.example.insight.viewmodel.MedicationLogsViewModel;

import java.util.ArrayList;

public class MedicationLogsFragment extends Fragment {

    private String medicationId;
    private FragmentMedicationLogsBinding binding;
    private MedicationLogAdapter adapter;
    private MedicationLogsViewModel viewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicationLogsBinding.inflate(inflater, container, false);
        setupRecyclerView();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationLogsViewModel.class);
        viewModel.getLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs != null && !logs.isEmpty()) {
                binding.textViewNoLogs.setVisibility(View.GONE);
                binding.recyclerViewLogs.setVisibility(View.VISIBLE);
                adapter.updateLogs(logs);
            } else {
                binding.textViewNoLogs.setVisibility(View.VISIBLE);
                binding.recyclerViewLogs.setVisibility(View.GONE);
            }
        });

        viewModel.fetchMedicationLogs(medicationId);
        return binding.getRoot();
    }

//    private void setupRecyclerView() {
//        adapter = new MedicationLogAdapter(requireContext(), new ArrayList<>());
//        binding.recyclerViewLogs.setAdapter(adapter);
//        binding.recyclerViewLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
//    }

    private void setupRecyclerView() {
        adapter = new MedicationLogAdapter(requireContext(), new ArrayList<>(), medicationId, new EditItemClickListener() {
            @Override
            public void OnClickEdit(View v, int pos) {
                MedicationLog log = adapter.getLogAt(pos);
                if (log != null) {
                    Intent intent = new Intent(requireContext(), EditLogActivity.class);
                    intent.putExtra("medicationID", medicationId); // you already have this
                    intent.putExtra("dosage", log.getDosage());    // if needed
                    intent.putExtra("logID", log.getLogId());      // pass the unique log ID
                    intent.putExtra("date", log.getFormattedDate());
                    intent.putExtra("time", log.getFormattedTime());
                    intent.putExtra("status", log.getStatus());    // "Taken" or "Missed"
                    requireContext().startActivity(intent);
                    Log.d("logAdapter", "Edit clicked for" + log.getDosage());
                }
            }

            @Override
            public void OnClickDelete(View v, int pos) {
                MedicationLog log = adapter.getLogAt(pos);
                if (log != null) {
                    viewModel.deleteLog(adapter.getMedicationId(), log.getLogId(), new MedicationLogUtils.MedicationLogCallback() {
                        @Override
                        public void onSuccess() {
                            adapter.removeLogAt(pos); // ✅ Only remove after Firestore confirms
                            Log.d("logAdapter", "Delete clicked for" + log.getDosage());                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(), "Failed to delete log", Toast.LENGTH_SHORT).show();
                            Log.e("LogAdapter", "❌ Firestore deletion failed", e);
                        }
                    });
                    // Optionally show confirmation

                }
            }

            @Override
            public void OnClickItem(View v, int pos) {
                MedicationLog log = adapter.getLogAt(pos);
                if (log != null) {
                    // Optional: show detail view
                    Log.d("logAdapter", "Item clicked for" + log.getDosage());
                }
            }
        });

        binding.recyclerViewLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewLogs.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Force a fresh fetch of logs every time the fragment comes into focus
        viewModel.fetchMedicationLogs(medicationId);
    }
}
