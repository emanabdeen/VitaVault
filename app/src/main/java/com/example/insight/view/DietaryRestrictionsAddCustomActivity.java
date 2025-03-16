package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.DietaryRestrictionCustomIngredientAdapter;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionsAddCustomActivity extends DrawerBaseActivity implements ItemClickListener {

    ActivityDietaryRestrictionsAddCustomBinding binding;
    DietaryRestrictionCustomIngredientAdapter ingredientAdapter;
    private DietaryRestrictionIngredientViewModel viewModel;
    List<DietaryRestrictionIngredient> ingredientList = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser user;
    DietaryRestrictionIngredient ingredient;
    final String  customCategory = "custom";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsAddCustomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");
        binding.textViewTitle.setText("Add Custom Dietary Restriction");

        viewModel = new ViewModelProvider(this).get(DietaryRestrictionIngredientViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DietaryRestrictionsAddCustomActivity.this, Login.class));
        }

        //Fetch ingredient list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.customIngredientList.setLayoutManager(layoutManager);


        //connects adapter with list item
        ingredientAdapter = new DietaryRestrictionCustomIngredientAdapter(getApplicationContext(), ingredientList);
        ingredientAdapter.setClickListener(this);
        binding.customIngredientList.setAdapter(ingredientAdapter);



        // Add new custom ingredient
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    try {
                        DietaryRestrictionIngredientViewModel viewModel = new DietaryRestrictionIngredientViewModel();
                        String uid = user.getUid();

                        String ingredientText = String.valueOf(binding.editIngredient.getText());

                        if (!ingredientText.isEmpty()) {

                            DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient(ingredientText, customCategory);


                                viewModel.addDietaryRestrictionIngredient(ingredient);
                                Toast.makeText(DietaryRestrictionsAddCustomActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                                showError(binding.errorIngredient, "", false);


                            //reloads the ingredient list for both scenarios
                            clear();
                            fetchIngredientList();
                        } else {

                            showError(binding.errorIngredient, "Required field cannot be empty", true);
                        }

                    } catch (Exception e) {
                        Log.e("error", "try-catch error: " + e.getMessage());
                    }
            }

        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchIngredientList();
    }

    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }


    private void fetchIngredientList(){

        ingredientList = new ArrayList<>();

        viewModel.getCustomDietaryRestrictionIngredients();
        viewModel.getCustomIngredientsData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {
                ingredientList.clear();
                ingredientList.addAll(ingredientData);//to reset the current list

                ingredientAdapter.updateIngredientList(ingredientList);

                Log.d("AddCustomIngredient", "ingredientList count > " + ingredientList.size());
            }
            else {
                ingredientList = new ArrayList<>();
                ingredientAdapter.updateIngredientList(ingredientList);
                Log.d("AddCustomIngredient", "ingredientList is null or empty.");
            }
        });
    }

    private void clear(){
        binding.editIngredient.setText("");
        showError(binding.errorIngredient, "", false);
    }


    @Override
    public void OnClickItem(View v, int pos) {

        if (user == null) {
            Intent intentObj = new Intent(this, Login.class);
            startActivity(intentObj);

        } else if (pos >= 0 && pos < ingredientList.size()) {
            String uid = user.getUid();

            DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient(ingredientList.get(pos).getIngredientName(), ingredientList.get(pos).getIngredientCategory());
            ingredient.setIngredientId(ingredientList.get(pos).getIngredientId());

            Intent editIngredientIntent = new Intent(this, DietaryRestrictionsEditCustomActivity.class);
            //Bundle bundle = new Bundle();
            //bundle.putSerializable("customDietaryRestriction", ingredient);
            editIngredientIntent.putExtra("customDietaryRestriction", ingredient);
            startActivity(editIngredientIntent);


        }

    }

    @Override
    public void OnClickDelete(View v, int pos) {

        if (user == null) {
            Intent intentObj = new Intent(this, Login.class);
            startActivity(intentObj);
            Log.d("tag", "Redirect user to login page");

        } else if (pos >= 0 && pos < ingredientList.size()) {
            String uid = user.getUid();
            String ingredientId = ingredientList.get(pos).getIngredientId();
            viewModel.deleteDietaryRestrictionIngredient(ingredientId);

            Toast.makeText(this, ingredientList.get(pos).getIngredientName()+ "deleted", Toast.LENGTH_LONG).show();

            //To refresh the recyclerview list
            //viewModel.getCustomDietaryRestrictionIngredients();
            fetchIngredientList();


        }

    }

}