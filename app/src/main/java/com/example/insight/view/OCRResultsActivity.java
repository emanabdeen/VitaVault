package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


        // Init readiness tracker
        ingredientScanViewModel.initReadyObservers(
                ingredientScanViewModel.getMatchedIngredientsData(),
                ingredientScanViewModel.getGeminiRawResponse()
        );

        ingredientScanViewModel.isDataReady().observe(this, ready -> {
            if (Boolean.TRUE.equals(ready)) {
                ingredientScanViewModel.setIsLoading(false); // Triggers fragment UI state update
            }
        });
        //gemini response
        ingredientScanViewModel.getGeminiRawResponse().observe(this, geminiResponse -> {
            if (geminiResponse != null && !geminiResponse.isEmpty()) {
                runOnUiThread(() -> {
                    try {
                        String cleanedJson = geminiResponse
                                .replaceAll("(?i)```json", "")
                                .replaceAll("```", "")
                                .trim();

                        Set<String> flaggedSet = new HashSet<>();
                        Map<String, List<String>> groupedResults = new HashMap<>();
                        Map<String, String> reasonMap = new HashMap<>();
                        JSONArray jsonArray = new JSONArray(cleanedJson);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            String ingredient = item.getString("ingredient").trim().toLowerCase();
                            String restriction = item.getString("restriction").trim();

                            flaggedSet.add(ingredient);
                            groupedResults.computeIfAbsent(ingredient, k -> new ArrayList<>()).add(restriction);
                            if (!reasonMap.containsKey(ingredient)) {
                                reasonMap.put(ingredient, restriction); // just the first reason, or format multiple if you want
                            }

                        }

                        ocrIngredientsListFragment.setGeminiFlaggedIngredients(flaggedSet);
                        ocrIngredientsListFragment.setGeminiFlagReasons(reasonMap);

                        // Reorder list
                        List<OcrIngredient> allMatchedIngredients = ingredientScanViewModel.getMatchedIngredientsData().getValue();
                        if (allMatchedIngredients != null) {
                            Set<String> geminiFlagged = flaggedSet; // already lowercased

                            // Split into flagged + safe while preserving original order
                            List<OcrIngredient> flaggedFirst = new ArrayList<>();
                            List<OcrIngredient> safe = new ArrayList<>();

                            for (int i = allMatchedIngredients.size() - 1; i >= 0; i--) {
                                OcrIngredient ingredient = allMatchedIngredients.get(i);
                                String name = ingredient.getIngredientName().toLowerCase();
                                boolean isFlagged = geminiFlagged.contains(name)
                                        || ingredient.isDietaryRestrictionFlagged()
                                        || ingredient.isSameNameAsCommonRestrictedIngredient();

                                if (isFlagged) {
                                    flaggedFirst.add(ingredient);
                                } else {
                                    safe.add(ingredient);
                                }
                            }

                            // Merge without reversing
                            List<OcrIngredient> finalList = new ArrayList<>();
                            finalList.addAll(flaggedFirst);
                            finalList.addAll(safe);

                            ocrIngredientsListFragment.updateIngredientsList(finalList);
                        }

                        // UI setup
                        SpannableStringBuilder resultText = new SpannableStringBuilder();
                        for (String ingredient : groupedResults.keySet()) {
                            String bullet = "• ";
                            String boldText = ingredient + " – ";
                            List<String> reasons = groupedResults.get(ingredient);
                            String reasonText = String.join("; ", reasons);

                            int start = resultText.length();
                            resultText.append(bullet).append(boldText);
                            resultText.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                                    start + bullet.length(), start + bullet.length() + boldText.length(),
                                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            resultText.append(reasonText).append("\n");
                        }

                        binding.aiResultDescription.setText(resultText);
                        binding.aiResultCard.setVisibility(View.VISIBLE);
                        binding.aiResultDescription.setVisibility(View.GONE);

                        binding.aiResultTitle.setOnClickListener(v -> {
                            if (binding.aiResultDescription.getVisibility() == View.GONE) {
                                binding.aiResultDescription.setVisibility(View.VISIBLE);
                                binding.aiResultTitle.setText("⚠️ AI Detected Potential Conflicts (Tap to collapse)");
                            } else {
                                binding.aiResultDescription.setVisibility(View.GONE);
                                binding.aiResultTitle.setText("⚠️ AI Detected Potential Conflicts (Tap to expand)");
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse Gemini JSON: " + e.getMessage());
                        binding.aiResultDescription.setText("⚠️ AI returned a result, but it couldn't be processed.");
                        binding.aiResultCard.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                runOnUiThread(() -> binding.aiResultCard.setVisibility(View.GONE));
            }
        });



        ingredientScanViewModel.isDataReady().observe(this, ready -> {
            if (Boolean.TRUE.equals(ready)) {
                //List<OcrIngredient> finalList = ingredientScanViewModel.getMatchedIngredientsData().getValue();
                //ocrIngredientsListFragment.updateIngredientsList(finalList);
                ingredientScanViewModel.setIsLoading(false); // Triggers fragment UI state update
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

                    //Check gemini
                    ingredientScanViewModel.analyzeWithGemini();
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