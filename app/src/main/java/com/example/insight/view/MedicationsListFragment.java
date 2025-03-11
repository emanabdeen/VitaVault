package com.example.insight.view;

import android.content.Intent;
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

import com.example.insight.adapter.MedicationsListAdapter;
import com.example.insight.databinding.FragmentMedicationsListBinding;
import com.example.insight.model.Medication;
import com.example.insight.viewmodel.MedicationViewModel;

import java.util.ArrayList;
import java.util.List;

public class MedicationsListFragment extends Fragment implements EditItemClickListener {

    private FragmentMedicationsListBinding binding;
    private MedicationViewModel viewModel;
    private List<Medication> medicationList = new ArrayList<>();
    private MedicationsListAdapter medicationsListAdapter;

    // Internal flags for loading and data
    private boolean isLoading = false;
    private List<Medication> currentMedications = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicationsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(MedicationViewModel.class);

        // Setup RecyclerView
        medicationsListAdapter = new MedicationsListAdapter(requireContext(), new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(medicationsListAdapter);

        // ✅ Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateUIState(); // Always re-evaluate UI when loading state changes
        });

        // ✅ Observe medication list
        viewModel.getMedicationsData().observe(getViewLifecycleOwner(), medications -> {
            currentMedications = medications != null ? medications : new ArrayList<>();
            medicationsListAdapter.updateData(currentMedications);
            updateUIState(); // Always re-evaluate UI when data changes
        });

        // ✅ Initial fetch (with loading spinner)
        viewModel.getMedications(true); // 'true' to show loading bar on first load

        // ✅ Search functionality
        binding.iconSearch.setOnClickListener(v -> {
            String searchQuery = binding.searchText.getText().toString().trim();
            filterMedications(searchQuery);
        });
    }

    // ✅ Combined UI state update for loading/data/empty
    private void updateUIState() {
        if (isLoading) {
            // Show loading spinner
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (!currentMedications.isEmpty()) {
                // Show list when data exists
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
            } else {
                // Show empty message when no data
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.VISIBLE);
                binding.emptyMessage.setText("No medications found.");
            }
        }
    }

    // ✅ Handle item click (navigate to logs)
    @Override
    public void OnClickItem(View v, int pos) {
        // ✅ Go to Logs Page
        if (currentMedications != null && pos < currentMedications.size()) {
            String medicationId = currentMedications.get(pos).getMedicationId();
            String medicationName = currentMedications.get(pos).getName();

            Intent intent = new Intent(requireContext(), MedicationLogsActivity.class);
            intent.putExtra("medicationID", medicationId);
            intent.putExtra("medicationName", medicationName);
            Log.d("MedicationLogsActivity", "Sent medicationId: " + medicationId);
            startActivity(intent);
        }
    }

    @Override
    public void OnClickEdit(View v, int pos) {
        // Open Edit
        if (currentMedications != null && pos < currentMedications.size()) {
            String medicationId = currentMedications.get(pos).getMedicationId();
            Intent intent = new Intent(requireContext(), MedicationDetails.class);
            intent.putExtra("medicationID", medicationId);
            startActivity(intent);
        }
    }

    @Override
    public void OnClickDelete(View v, int pos) {
        // Handle delete
        if (currentMedications != null && pos < currentMedications.size()) {
            Medication medication = currentMedications.get(pos);
            viewModel.prepareToRemoveMedication(medication);
        }
    }

    // ✅ Called by Activity to refresh list dynamically (optional)
    public void updateMedicationList(List<Medication> medications) {
        if (medications != null) {
            currentMedications = medications;
            medicationsListAdapter.updateData(medications);
            medicationsListAdapter.notifyDataSetChanged();
            updateUIState(); // Re-evaluate UI when updated
        }
    }

    // ✅ Search filter
    private void filterMedications(String query) {
        List<Medication> filteredList = new ArrayList<>();
        for (Medication medication : currentMedications) {
            if (medication.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(medication);
            }
        }

        medicationsListAdapter.updateData(filteredList);

        // Show appropriate UI for filtered results
        if (filteredList.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.VISIBLE);
            binding.emptyMessage.setText("No medications match your search.");
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyMessage.setVisibility(View.GONE);
        }
    }
}
