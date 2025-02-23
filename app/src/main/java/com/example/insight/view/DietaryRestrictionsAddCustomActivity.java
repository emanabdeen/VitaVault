package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adaoter.DietaryRestrictionCustomIngredientAdapter;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.model.DietaryResrtictionIngredient;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

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

        viewModel = new DietaryRestrictionIngredientViewModel();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DietaryRestrictionsAddCustomActivity.this, Login.class));
        }

        //Fetch ingredient list
        viewModel.GetAllDietaryRestrictionIngredients();
        observeIngredientList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        binding.customIngredientList.setLayoutManager(layoutManager);


        //connects adapter with list item
        ingredientAdapter = new DietaryRestrictionCustomIngredientAdapter(getApplicationContext(), ingredientList);
        binding.customIngredientList.setAdapter(ingredientAdapter);


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
                       observeIngredientList();

                   }else{

                       showError(binding.errorIngredient, "Required field cannot be empty",true);
                   }




               }catch (Exception e){
                   Log.e("error", "try-catch error: "+ e.getMessage());
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


    private void observeIngredientList(){

        viewModel.getIngredientsData().observe(this, ingredientData -> {
            if (ingredientData != null || !ingredientData.isEmpty()) {
                ingredientData.clear();//to reset the current list
                ingredientList.addAll(ingredientData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("AddCustomIngredient", "vitalsList count>> " + ingredientList.size());
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