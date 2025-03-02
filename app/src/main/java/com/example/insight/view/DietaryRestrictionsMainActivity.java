package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.adapter.DietaryRestrictionCustomIngredientAdapter;
import com.example.insight.adapter.DietaryRestrictionPredefinedGroupAdapter;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.databinding.ActivityDietaryRestrictionsMainBinding;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.viewmodel.dietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class DietaryRestrictionsMainActivity extends DrawerBaseActivity implements GroupedRecyclerViewItemClickListener {

    ActivityDietaryRestrictionsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DietaryRestrictionsMainActivity.this, Login.class));
        }

        binding = ActivityDietaryRestrictionsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");

        restrictionsMap = CommonRestrictedIngredients.GetAllIngredientsWithCategory();

       // viewModel = new ViewModelProvider(this).get(dietaryRestrictionIngredientViewModel.class);

        //Creates the future "inner" recyclerViewer with 2 columns.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.externalGroupedList.setLayoutManager(layoutManager);

        //connects adapter with list item

        RecyclerView mainList = findViewById(R.id.externalGroupedList);
        DietaryRestrictionPredefinedGroupAdapter groupedIngredientsAdapter = new DietaryRestrictionPredefinedGroupAdapter(this,restrictionsMap,this);
        mainList.setAdapter(groupedIngredientsAdapter);




        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), DietaryRestrictionsAddCustomActivity.class);
                startActivity(intentObj);
            }
        });

    }


    @Override
    public void onItemClick(int outerPos, int innerPos) {
        RestrictedIngredientsCategory category = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[outerPos];
        List<CommonRestrictedIngredients> itemList = restrictionsMap.get(category);

        // Use innerPos to get the clicked item
        CommonRestrictedIngredients clickedItem = itemList.get(innerPos);

        // Do something with the clicked item
        Toast.makeText(this, "Clicked item: " + clickedItem.getIngredientDescription(), Toast.LENGTH_SHORT).show();

        Log.e("PredefItem - MAIN", category.getCategoryDescription() +" "+ clickedItem.getIngredientDescription() );

    }
}