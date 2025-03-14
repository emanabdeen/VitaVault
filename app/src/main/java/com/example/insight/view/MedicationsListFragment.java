package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    // Keep a full copy of medications for filtering
    private List<Medication> allMedications = new ArrayList<>();
    // List shown in the adapter (possibly filtered)
    private List<Medication> currentMedications = new ArrayList<>();
    private MedicationsListAdapter medicationsListAdapter;

    private boolean isLoading = false;

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

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateUIState();
        });

        // Observe medication data; store a full copy for filtering
        viewModel.getMedicationsData().observe(getViewLifecycleOwner(), medications -> {
            allMedications = (medications != null) ? new ArrayList<>(medications) : new ArrayList<>();
            currentMedications = new ArrayList<>(allMedications);
            medicationsListAdapter.updateData(currentMedications);
            updateUIState();
        });

        // Initial fetch with loading indicator
        viewModel.getMedications(true);

        // Search functionality: when search icon is clicked
        binding.iconSearch.setOnClickListener(v -> {
            String searchQuery = binding.searchText.getText().toString().trim();
            filterMedications(searchQuery);
        });

        // Listen for text changes to show/hide the clear filter button.
        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* No op */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If text exists, show the clear button; if not, hide it and restore full list.
                if (s.length() > 0) {
                    binding.textClear.setVisibility(View.VISIBLE);
                } else {
                    binding.textClear.setVisibility(View.INVISIBLE);
                    resetMedicationList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* No op */ }
        });

// Clear button: clear the text and reset the list when tapped.
        binding.textClear.setOnClickListener(v -> {
            binding.searchText.setText("");
            resetMedicationList();
        });

    }

    // Reset the displayed list to the full medication list.
    private void resetMedicationList() {
        currentMedications = new ArrayList<>(allMedications);
        medicationsListAdapter.updateData(currentMedications);
        medicationsListAdapter.notifyDataSetChanged();
        updateUIState();
    }


    // Filter medications based on query (using the full list).
    private void filterMedications(String query) {
        List<Medication> filteredList = new ArrayList<>();
        for (Medication medication : allMedications) {
            if (medication.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(medication);
            }
        }
        currentMedications = filteredList;
        medicationsListAdapter.updateData(filteredList);
        medicationsListAdapter.notifyDataSetChanged();

        // Show empty message if no matches.
        if (filteredList.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.VISIBLE);
            binding.emptyMessage.setText("No medications match your search.");
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyMessage.setVisibility(View.GONE);
        }
    }

    // Update UI elements based on loading state and data availability.
    private void updateUIState() {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (!currentMedications.isEmpty()) {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.VISIBLE);
                binding.emptyMessage.setText("No medications found.");
            }
        }
    }

    @Override
    public void OnClickItem(View v, int pos) {
        if (currentMedications != null && pos < currentMedications.size()) {
            String medicationId = currentMedications.get(pos).getMedicationId();
            String medicationName = currentMedications.get(pos).getName();

            Intent intent = new Intent(requireContext(), MedicationLogsActivity.class);
            intent.putExtra("medicationID", medicationId);
            intent.putExtra("medicationName", medicationName);
            startActivity(intent);
        }
    }

    @Override
    public void OnClickEdit(View v, int pos) {
        if (currentMedications != null && pos < currentMedications.size()) {
            String medicationId = currentMedications.get(pos).getMedicationId();
            Intent intent = new Intent(requireContext(), MedicationDetails.class);
            intent.putExtra("medicationID", medicationId);
            startActivity(intent);
        }
    }

    @Override
    public void OnClickDelete(View v, int pos) {
        if (currentMedications != null && pos < currentMedications.size()) {
            Medication medication = currentMedications.get(pos);
            viewModel.prepareToRemoveMedication(medication);
        }
    }

    public void clearFilter() {
        // Clear the filter EditText
        binding.searchText.setText("");
    }

}
