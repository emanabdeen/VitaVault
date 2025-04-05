package com.example.insight.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.databinding.FragmentOcringredientListBinding;
import com.example.insight.adapter.OcrIngredientListAdapter;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.model.OcrIngredient;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.example.insight.viewmodel.IngredientScanViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OcrIngredientsListFragment extends Fragment implements ItemClickListener {
    private static final String TAG = "OcrIngredientsListFragment";
    private FragmentOcringredientListBinding binding;
    private IngredientScanViewModel ingredientScanViewModel;
    private boolean isLoading = false;
    private DietaryRestrictionIngredientViewModel dietaryRestrictionsViewModel;
    private List<OcrIngredient> allIngredientsList, currentIngredientsList;
    private OcrIngredientListAdapter ocrIngredientListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOcringredientListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        ingredientScanViewModel = new ViewModelProvider(requireActivity()).get(IngredientScanViewModel.class);
        dietaryRestrictionsViewModel = new ViewModelProvider(requireActivity()).get(DietaryRestrictionIngredientViewModel.class);

        // Setup RecyclerView
        ocrIngredientListAdapter = new OcrIngredientListAdapter(requireContext(), new ArrayList<>());
        ocrIngredientListAdapter.setClickListener(this);
        binding.ocrIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.ocrIngredientRecyclerView.setAdapter(ocrIngredientListAdapter);


        binding.iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = binding.searchText.getText().toString().trim();
                filterIngredients(query);
            }
        });
        ingredientScanViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            updateUIState();
        });

        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* No op */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If text exists, show the clear button; if not, hide it and restore full list.
                if (s.length() > 0) {
                    binding.btnClearSearch.setVisibility(View.VISIBLE);
                } else {
                    binding.btnClearSearch.setVisibility(View.INVISIBLE);
                    resetIngredientsList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* No op */ }
        });
        binding.btnClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });
    }

    // Reset the displayed list to the full ingredients list.
    private void resetIngredientsList() {
        currentIngredientsList = new ArrayList<>(allIngredientsList);
        ocrIngredientListAdapter.updateData(currentIngredientsList);
        ocrIngredientListAdapter.notifyDataSetChanged();
        updateUIState();
    }

    @Override
    public void OnClickItem(View v, int pos) {
        // add ingredient to custom ingredients from list
        DietaryRestrictionIngredient newCustomIngredient = new DietaryRestrictionIngredient();
        newCustomIngredient.setIngredientName(allIngredientsList.get(pos).getIngredientName());
        newCustomIngredient.setIngredientCategory("custom");

        dietaryRestrictionsViewModel.addDietaryRestrictionIngredient(newCustomIngredient);
        Log.d(TAG, "added new custom ingredient from ocr scan result list" + newCustomIngredient.getIngredientName());

        // Change icon to checkmark to show that ingredient has been added
        allIngredientsList.get(pos).setAddedAsCustom(true);
        updateIngredientsList(allIngredientsList);

    }

    @Override
    public void OnClickDelete(View v, int pos) {

    }

    /**
     * Allows OcrResultsActivity to refresh the RecyclerView dynamically.
     */
    public void updateIngredientsList(List<OcrIngredient> ingredients) {
        if (ingredients != null) {
            allIngredientsList = ingredients;
            currentIngredientsList = new ArrayList<>(ingredients); // add this
            ocrIngredientListAdapter.updateData(currentIngredientsList);
            ocrIngredientListAdapter.notifyDataSetChanged(); // refresh UI
            updateUIState(); //force UI update here
        } else {
            Log.d(TAG, "updateIngredientsList: ingredients list is null");
        }
    }

    private void filterIngredients(String query) {
        currentIngredientsList = new ArrayList<>();

        if (allIngredientsList != null) {
            for (OcrIngredient ingredient : allIngredientsList) {
                if (ingredient.getIngredientName().toLowerCase().contains(query.toLowerCase())) {
                    currentIngredientsList.add(ingredient);
                }
            }
        }
        ocrIngredientListAdapter.updateData(currentIngredientsList);

        // Show/hide the "No ingredients found" message
        if (currentIngredientsList.isEmpty()) {
            binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.VISIBLE);
            binding.emptyMessage.setText("No ingredients match your search.");
        } else {
            binding.ocrIngredientRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyMessage.setVisibility(View.GONE);
        }
    }

    public void clearFilter() {
        binding.searchText.setText("");
        resetIngredientsList();
    }

    // Update UI elements based on loading state and data availability.
    private void updateUIState() {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
            binding.ocrResultListView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);

            if (currentIngredientsList != null && !currentIngredientsList.isEmpty()) {
                binding.ocrResultListView.setVisibility(View.VISIBLE);
                binding.ocrIngredientRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
            } else {
                binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
                binding.ocrResultListView.setVisibility(View.GONE); // optional
                binding.emptyMessage.setVisibility(View.VISIBLE);
                binding.emptyMessage.setText("No ingredients found.");
            }
        }
    }


    public void setGeminiFlaggedIngredients(Set<String> flaggedIngredients) {
        if (ocrIngredientListAdapter != null && flaggedIngredients != null) {
            ocrIngredientListAdapter.setGeminiFlaggedIngredients(flaggedIngredients);
            ocrIngredientListAdapter.notifyDataSetChanged(); // Refresh UI
        }
    }

    public void setGeminiFlagReasons(Map<String, String> flagReasons) {
        if (ocrIngredientListAdapter != null) {
            ocrIngredientListAdapter.setGeminiFlagReasons(flagReasons);
        }
    }


}
