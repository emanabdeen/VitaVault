package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.R;
import com.example.insight.adapter.OcrIngredientListAdapter;
import com.example.insight.databinding.ActivityOcrResultsBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.model.Medication;
import com.example.insight.model.OcrIngredient;
import com.example.insight.utility.IngredientUtils;
import com.example.insight.viewmodel.IngredientScanViewModel;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OCRResultsActivity extends DrawerBaseActivity {
    private static final String TAG = "OCRResultsActivity";

    private ActivityOcrResultsBinding binding;
    private IngredientScanViewModel ingredientScanViewModel;
    private OcrIngredientsListFragment ocrIngredientsListFragment;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOcrResultsBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());
        allocateActivityTitle("Ingredient Scan Results");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Redirect to login if user is not authenticated
        if (user == null) {
            finish();
            startActivity(new Intent(OCRResultsActivity.this, Login.class));
            return;
        }

        // Create and load the MedicationsListFragment by default
        ocrIngredientsListFragment = new OcrIngredientsListFragment();
        replaceFragment(ocrIngredientsListFragment);

        ingredientScanViewModel = new ViewModelProvider(this).get(IngredientScanViewModel.class);

        ingredientScanViewModel.getMatchedIngredientsData().observe(this, new Observer<List<OcrIngredient>>() {
            @Override
            public void onChanged(List<OcrIngredient> matchedIngredients) {
                if (matchedIngredients != null || !matchedIngredients.isEmpty()) {
                    // Update the adapter with the new data
                    ocrIngredientsListFragment.updateIngredientsList(matchedIngredients);
                }
                else {
                    Log.e(TAG, "matchedIngredientsData onChanged: matchedIngredients is null or empty");
                }
            }
        });

        if (getIntent() != null) {
            // Fetch ingredients from intent
            ingredientScanViewModel.scanIngredientsForMatches(getIntent(), db, user.getUid()).whenComplete((result, error) -> {
                if (error != null) {
                    Log.e(TAG, "Error scanning ingredients: " + error.getMessage());
                }
                if (result) {
                    Log.d(TAG, "Ingredients scanned successfully");
                    // Update the adapter with the new data
                    ocrIngredientsListFragment.updateIngredientsList(ingredientScanViewModel.getMatchedIngredientsData().getValue());
                }
            });
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        ingredientScanViewModel.getMatchedIngredientsData().observe(this, new Observer<List<OcrIngredient>>() {
            @Override
            public void onChanged(List<OcrIngredient> ocrIngredients) {
                // Fetch the latest data when returning

            }
        });
    }
    /**
     * Replaces the fragment container with the provided fragment.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentLayout, fragment);
        ft.commit();
    }
}