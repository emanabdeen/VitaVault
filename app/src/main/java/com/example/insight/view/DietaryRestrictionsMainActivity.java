package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.adapter.DietaryRestrictionCustomIngredientAdapter;

import com.example.insight.adapter.DietaryRestrictionPredefinedItemAdapter;
import com.example.insight.databinding.ActivityDietaryRestrictionsAddCustomBinding;
import com.example.insight.databinding.ActivityDietaryRestrictionsMainBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.viewmodel.dietaryRestrictionIngredientViewModel;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.context.AttributeContext;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DietaryRestrictionsMainActivity extends DrawerBaseActivity implements MultipleRecyclerViewItemClickListener {

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
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //binding.externalGroupedList.setLayoutManager(layoutManager);

        //connects adapter with list item

        /*
        RecyclerView mainList = findViewById(R.id.externalGroupedList);
        DietaryRestrictionPredefinedGroupAdapter groupedIngredientsAdapter = new DietaryRestrictionPredefinedGroupAdapter(this,this,flattenedList);
        mainList.setAdapter(groupedIngredientsAdapter);
*/


        int i = 0;
        for(Map.Entry<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> entry : restrictionsMap.entrySet()){

            try {

                RestrictedIngredientsCategory ingredientsCategory = entry.getKey();
                List<CommonRestrictedIngredients> commonRestrictedIngredientsList = entry.getValue();

                //dynamically fills each category label
                String fieldName = "group"+(i+1);


                Field field = binding.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                //sets group name
                TextView textView = (TextView) field.get(binding);
                textView.setText(ingredientsCategory.getCategoryDescription());



                //parse Enum to Bean
                List<DietaryRestrictionIngredient> ingredientListBean = new ArrayList<>();

                for(CommonRestrictedIngredients ingredient : commonRestrictedIngredientsList){
                    DietaryRestrictionIngredient newIngredient = new DietaryRestrictionIngredient();
                    newIngredient.setIngredientName(ingredient.getIngredientDescription());
                    newIngredient.setIngredientCategory(ingredientsCategory.getCategoryDescription());
                    ingredientListBean.add(newIngredient);
                }



                String recyclerName = "groupList"+(i+1);


               //dynamically sets the adapter for each one of the recyclerViews
                Field recyclerView = binding.getClass().getDeclaredField(recyclerName);
                recyclerView.setAccessible(true);

                RecyclerView recycler = (RecyclerView) recyclerView.get(binding);
                Log.e("TEST",recyclerView.getName());
                DietaryRestrictionPredefinedItemAdapter groupedIngredientsAdapter = new DietaryRestrictionPredefinedItemAdapter(this,ingredientListBean, this,recyclerView.getName());


                recycler.setAdapter(groupedIngredientsAdapter);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recycler.setLayoutManager(layoutManager);


            } catch (NoSuchFieldException e) {
                Log.e("DietaryMain", e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            i++;

            //// end of dynamically setting recycler views



            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), DietaryRestrictionsAddCustomActivity.class);
                startActivity(intentObj);
            }
        });

    }

/*
    @Override
    public void onItemClick(View v, int outerPos, int innerPos) {
      /*
        RestrictedIngredientsCategory category = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[outerPos];
        List<CommonRestrictedIngredients> itemList = restrictionsMap.get(category);

        // Use innerPos to get the clicked item
        CommonRestrictedIngredients clickedItem = itemList.get(innerPos);

        // Do something with the clicked item
        Toast.makeText(this, "Clicked item: " + clickedItem.getIngredientDescription(), Toast.LENGTH_SHORT).show();

        Log.e("PredefItem - MAIN", category.getCategoryDescription() +" "+ clickedItem.getIngredientDescription() );
*/

        /*
        Object clickedItem = flattenedList.get(outerPos);
        if (clickedItem instanceof RestrictedIngredientsCategory) {
            // Handle category click
            RestrictedIngredientsCategory category = (RestrictedIngredientsCategory) clickedItem;
            Toast.makeText(this, "Clicked category: " + category.getCategoryDescription(), Toast.LENGTH_SHORT).show();
        } else if (clickedItem instanceof CommonRestrictedIngredients) {
            // Handle item click
            CommonRestrictedIngredients item = (CommonRestrictedIngredients) clickedItem;
            Toast.makeText(this, "Clicked item: " + item.getIngredientDescription(), Toast.LENGTH_SHORT).show();
        }
        */

    }


    @Override
    public void onItemClick(View v,int position, String recyclerViewId) {
        Log.e("TEST", "There was a click!!");
        Log.e("TEST", String.valueOf(position));
        Log.e("TEST",recyclerViewId);
    }
}