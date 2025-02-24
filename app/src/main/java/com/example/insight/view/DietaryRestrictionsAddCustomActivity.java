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
import com.example.insight.model.DietaryResrtictionIngredient;
import com.example.insight.model.Vital;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionsAddCustomActivity extends DrawerBaseActivity{

    ActivityDietaryRestrictionsAddCustomBinding binding;
    DietaryRestrictionCustomIngredientAdapter ingredientAdapter;
    private DietaryRestrictionIngredientViewModel viewModel;
    List<DietaryResrtictionIngredient> ingredientList = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsAddCustomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");

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
        binding.customIngredientList.setAdapter(ingredientAdapter);

        fetchIngredientList();

        // Add new custom ingredient
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               try{
                   DietaryRestrictionIngredientViewModel viewModel = new DietaryRestrictionIngredientViewModel();
                   String uid = user.getUid();


                   String ingredientText = String.valueOf(binding.editIngredient.getText());

                   if(!ingredientText.isEmpty()){

                       DietaryResrtictionIngredient ingredient = new DietaryResrtictionIngredient(ingredientText, "Custom");

                       viewModel.AddDietaryRestrictionIngredient(ingredient);
                       Toast.makeText(DietaryRestrictionsAddCustomActivity.this, "Saved Successfully",Toast.LENGTH_LONG).show();
                       showError(binding.errorIngredient, "",false);

                       //reloads the ingredient list
                       clear();
                       fetchIngredientList();

                   }else{

                       showError(binding.errorIngredient, "Required field cannot be empty",true);
                   }

               }catch (Exception e){
                   Log.e("error", "try-catch error: "+ e.getMessage());
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


    private void fetchIngredientList(){

        viewModel.GetAllDietaryRestrictionIngredients();
        viewModel.getIngredientsData().observe(this, ingredientData -> {
            if (ingredientData != null || !ingredientData.isEmpty()) {
                ingredientList = ingredientData;//to reset the current list

                ingredientAdapter.updateMovies(ingredientList);


                Log.d("AddCustomIngredient", "ingredientList count>> " + ingredientList.size());
            }
            else {
                Log.d("AddCustomIngredient", "ingredientList is null or empty.");
            }
        });
    }

    private void clear(){
        binding.editIngredient.setText("");
        showError(binding.errorIngredient, "", false);
    }



}