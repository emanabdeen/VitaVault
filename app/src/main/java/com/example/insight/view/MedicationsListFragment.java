package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
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

public class MedicationsListFragment extends Fragment implements ItemClickListener {

    private FragmentMedicationsListBinding binding;
    private MedicationViewModel viewModel;
    private List<Medication> medicationList;
    private MedicationsListAdapter medicationsListAdapter;

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
        medicationsListAdapter = new MedicationsListAdapter(requireContext(), new ArrayList<>());
        medicationsListAdapter.setClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(medicationsListAdapter);

        // Observe LiveData for Medications List
        viewModel.getMedicationsData().observe(getViewLifecycleOwner(), medications -> {
            if (medications == null) {
                // Show progress bar while loading
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.GONE);
            } else if (!medications.isEmpty()) {
                // Hide progress bar when medications are loaded
                medicationList = medications;
                medicationsListAdapter.updateData(medications);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                // Hide progress bar and show "No medications found" message
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.VISIBLE);
                binding.emptyMessage.setText("No medications found.");
                binding.progressBar.setVisibility(View.GONE);
            }
        });


        // Fetch Medications from Firestore
        viewModel.getMedications();

        binding.iconSearch.setOnClickListener(v -> {
            String searchQuery = binding.searchText.getText().toString().trim();
            filterMedications(searchQuery);
        });
    }

    @Override
    public void OnClickItem(View v, int pos) {
        if (medicationList != null && pos < medicationList.size()) {
            String medicationId = medicationList.get(pos).getMedicationId();

            // Navigate to MedicationDetails page
            Intent intent = new Intent(requireContext(), MedicationDetails.class);
            intent.putExtra("medicationID", medicationId);
            startActivity(intent);
        }
    }

    @Override
    public void OnClickDelete(View v, int pos) {
        if (medicationList != null && pos < medicationList.size()) {
            String medicationId = medicationList.get(pos).getMedicationId();
            viewModel.removeMedication(medicationId);
        }
    }

    /**
     * Allows MedicationsActivity to refresh the RecyclerView dynamically.
     */
    public void updateMedicationList(List<Medication> medications) {
        if (medications != null) {
            medicationList = medications;
            medicationsListAdapter.updateData(medications);
            medicationsListAdapter.notifyDataSetChanged(); // Ensure UI refresh
        }
    }

    private void filterMedications(String query) {
        List<Medication> filteredList = new ArrayList<>();

        if (medicationList != null) {
            for (Medication medication : medicationList) {
                if (medication.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(medication);
                }
            }
        }

        medicationsListAdapter.updateData(filteredList);

        // Show/hide the "No medications found" message
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
