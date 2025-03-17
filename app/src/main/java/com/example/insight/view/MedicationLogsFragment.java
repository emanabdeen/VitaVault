package com.example.insight.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.MedicationLogAdapter;
import com.example.insight.databinding.FragmentMedicationLogsBinding;
import com.example.insight.model.MedicationLog;
import com.example.insight.viewmodel.MedicationLogsViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private void setupRecyclerView() {
        adapter = new MedicationLogAdapter(requireContext(), new ArrayList<>());
        binding.recyclerViewLogs.setAdapter(adapter);
        binding.recyclerViewLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Force a fresh fetch of logs every time the fragment comes into focus
        viewModel.fetchMedicationLogs(medicationId);
    }
}
