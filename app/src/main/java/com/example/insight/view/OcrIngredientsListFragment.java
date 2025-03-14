package com.example.insight.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.R;
import com.example.insight.databinding.FragmentOcringredientListBinding;
import com.example.insight.adapter.OcrIngredientListAdapter;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.model.OcrIngredient;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.example.insight.viewmodel.IngredientScanViewModel;

import java.util.ArrayList;
import java.util.List;

public class OcrIngredientsListFragment extends Fragment implements ItemClickListener {
    private static final String TAG = "OcrIngredientsListFragment";
    private FragmentOcringredientListBinding binding;
    private IngredientScanViewModel ingredientScanViewModel;
    private DietaryRestrictionIngredientViewModel dietaryRestrictionsViewModel;
    private List<OcrIngredient> matchedIngredientsList;
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

        // Observe LiveData for scanned ingredients List
        ingredientScanViewModel.getMatchedIngredientsData().observe(getViewLifecycleOwner(), matchedIngredients -> {
            if (matchedIngredients == null) {
                // Show progress bar while loading
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.GONE);
            } else if (!matchedIngredients.isEmpty()) {
                // Hide progress bar when medications are loaded
                matchedIngredientsList = matchedIngredients;
                ocrIngredientListAdapter.updateData(matchedIngredientsList);
                binding.ocrIngredientRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                // Hide progress bar and show "No medications found" message
                binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
                binding.emptyMessage.setVisibility(View.VISIBLE);
                binding.emptyMessage.setText("No ingredients found.");
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void OnClickItem(View v, int pos) {
        // add ingredient to custom ingredients from list
        DietaryRestrictionIngredient newCustomIngredient = new DietaryRestrictionIngredient();
        newCustomIngredient.setIngredientName(matchedIngredientsList.get(pos).getIngredientName());
        newCustomIngredient.setIngredientCategory("custom");

        dietaryRestrictionsViewModel.addDietaryRestrictionIngredient(newCustomIngredient);
        Log.d(TAG, "added new custom ingredient from ocr scan result list" + newCustomIngredient.getIngredientName());

        // Change icon to checkmark to show that ingredient has been added
        ImageButton btnAdd = binding.ocrIngredientRecyclerView.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.btnAdd);
        btnAdd.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnAdd.setImageResource(R.drawable.check_mark);
    }

    @Override
    public void OnClickDelete(View v, int pos) {

    }

    /**
     * Allows OcrResultsActivity to refresh the RecyclerView dynamically.
     */
    public void updateIngredientsList(List<OcrIngredient> ingredients) {
        if (ingredients != null) {
            matchedIngredientsList = ingredients;
            ocrIngredientListAdapter.updateData(ingredients);
            ocrIngredientListAdapter.notifyDataSetChanged(); // Ensure UI refresh
        }
        else {
            Log.d(TAG, "updateIngredientsList: ingredients list is null");
        }
    }

    private void filterIngredients(String query) {
        List<OcrIngredient> filteredList = new ArrayList<>();

        if (matchedIngredientsList != null) {
            for (OcrIngredient ingredient : matchedIngredientsList) {
                if (ingredient.getIngredientName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(ingredient);
                }
            }
        }
        ocrIngredientListAdapter.updateData(filteredList);

        // Show/hide the "No ingredients found" message
        if (filteredList.isEmpty()) {
            binding.ocrIngredientRecyclerView.setVisibility(View.GONE);
            binding.emptyMessage.setVisibility(View.VISIBLE);
            binding.emptyMessage.setText("No ingredients match your search.");
        } else {
            binding.ocrIngredientRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyMessage.setVisibility(View.GONE);
        }
    }
}
