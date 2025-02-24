package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.R;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.viewmodel.dietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DietaryRestrictionsEditCustomActivity extends DrawerBaseActivity {


        ActivityDietaryRestrictionsAddCustomBinding binding;
        private dietaryRestrictionIngredientViewModel viewModel;
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

            viewModel = new ViewModelProvider(this).get(dietaryRestrictionIngredientViewModel.class);

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
                        dietaryRestrictionIngredientViewModel viewModel = new dietaryRestrictionIngredientViewModel();
                        String uid = user.getUid();

                        String ingredientText = String.valueOf(binding.editIngredient.getText());

                        if (!ingredientText.isEmpty()) {

                            //updated object property "name" with the one from the screen
                            ingredient.setIngredientName(ingredientText);

                            Log.e("AddCustomIngredient", "EDIT!!!!");
                            viewModel.updateDietaryRestrictionIngredient(ingredient);
                            finish();
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


    private void showError(TextView errorView, String message, boolean isVisible) {
            if (isVisible) {
                errorView.setText(message);
                errorView.setVisibility(View.VISIBLE);
            } else {
                errorView.setVisibility(View.GONE);
            }
        }

}

