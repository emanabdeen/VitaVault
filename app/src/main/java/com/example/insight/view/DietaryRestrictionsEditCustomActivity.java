package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class DietaryRestrictionsEditCustomActivity extends DrawerBaseActivity {


    ActivityDietaryRestrictionsAddCustomBinding binding;
    private DietaryRestrictionIngredientViewModel viewModel;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsAddCustomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");
        binding.textViewTitle.setText("Update Custom Dietary Restriction");

        viewModel = new ViewModelProvider(this).get(DietaryRestrictionIngredientViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(com.example.insight.view.DietaryRestrictionsEditCustomActivity.this, Login.class));
        }

        //Fetch ingredient list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.customIngredientList.setLayoutManager(layoutManager);


        //Get information from intent
        Intent intentData = getIntent();
        ingredient = (DietaryRestrictionIngredient) intentData.getSerializableExtra("customDietaryRestriction");

        assert ingredient != null;
        binding.editIngredient.setText(ingredient.getIngredientName());

        // Add new custom ingredient
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    DietaryRestrictionIngredientViewModel viewModel = new DietaryRestrictionIngredientViewModel();
                    String uid = user.getUid();

                    String ingredientText = String.valueOf(binding.editIngredient.getText());

                    if (!ingredientText.isEmpty()) {

                        //updated object property "name" with the one from the screen
                        ingredient.setIngredientName(ingredientText.trim().toLowerCase());

                        CompletableFuture<String> isRestrictionDuplicated = viewModel.checkIfIngredientExists(ingredient);

                        Log.i("CheckExisting", "EDIT: " + String.valueOf(isRestrictionDuplicated.isDone()));

                        isRestrictionDuplicated.whenComplete((result, error) -> {

                            Log.i("CheckExisting", "EDIT: " + String.valueOf(result));

                            if (result != null) {
                                //checking if it's the object itself, as firebase doesn't allow comparing equals and notEquals in the same query.
                                if (!result.equals(ingredient.getIngredientId())) {
                                    Toast.makeText(DietaryRestrictionsEditCustomActivity.this, "Cannot add duplicated ingredient: " + ingredient.getIngredientName(), Toast.LENGTH_SHORT).show();
                                }else{
                                    //if it's the object itself, proceed.
                                    viewModel.updateDietaryRestrictionIngredient(ingredient);
                                    finish();
                                }
                            } else {
                                viewModel.updateDietaryRestrictionIngredient(ingredient);
                                finish();
                            }

                        });
                    } else {

                        showError(binding.errorIngredient, "Required field cannot be empty", true);
                    }

                } catch (Exception e) {
                    Log.e("error", "try-catch error: " + e.getMessage());
                }
            }

        });

    }


    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

}

