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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DietaryRestrictionsMainActivity extends DrawerBaseActivity implements MultipleRecyclerViewItemClickListener {

    ActivityDietaryRestrictionsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;
    private dietaryRestrictionIngredientViewModel viewModel;

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

        viewModel = new ViewModelProvider(this).get(dietaryRestrictionIngredientViewModel.class);


        int i = 0;
        for (Map.Entry<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> entry : restrictionsMap.entrySet()) {

            try {

                RestrictedIngredientsCategory ingredientsCategory = entry.getKey();
                List<CommonRestrictedIngredients> commonRestrictedIngredientsList = entry.getValue();

                //dynamically fills each category label
                String fieldName = "group" + (i + 1);


                Field field = binding.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                //sets group name
                TextView textView = (TextView) field.get(binding);
                textView.setText(ingredientsCategory.getCategoryDescription());


                //parse Enum to Bean
                List<DietaryRestrictionIngredient> ingredientListBean = new ArrayList<>();

                for (CommonRestrictedIngredients ingredient : commonRestrictedIngredientsList) {
                    DietaryRestrictionIngredient newIngredient = new DietaryRestrictionIngredient();
                    newIngredient.setIngredientName(ingredient.getIngredientDescription());
                    newIngredient.setIngredientCategory(ingredientsCategory.getCategoryDescription());
                    ingredientListBean.add(newIngredient);
                }


                String recyclerName = "groupList" + (i + 1);


                //dynamically sets the adapter for each one of the recyclerViews
                Field recyclerView = binding.getClass().getDeclaredField(recyclerName);
                recyclerView.setAccessible(true);

                RecyclerView recycler = (RecyclerView) recyclerView.get(binding);

                DietaryRestrictionPredefinedItemAdapter groupedIngredientsAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, recyclerView.getName());


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

    }


    @Override
    public void onItemClick(View v, int position, String recyclerViewId, boolean isChecked) {


        //based on the list, identifies the category in the map

        String[] categoryPosition = recyclerViewId.split("groupList");

        if (categoryPosition.length > 1) {

            //decrease 1 as map index starts in 0, and groupList starts as 1.
            int mapCategoryPosition = Integer.parseInt(categoryPosition[1])-1;
            RestrictedIngredientsCategory selectedCategory = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[mapCategoryPosition];
            List<CommonRestrictedIngredients> selectedIngredientList = restrictionsMap.get(selectedCategory);
            CommonRestrictedIngredients selectedItem = selectedIngredientList.get(position);

            DietaryRestrictionIngredient selectedIngredient = new DietaryRestrictionIngredient(selectedItem.getIngredientDescription(),selectedCategory.getCategoryDescription() );

            if (isChecked) {
                viewModel.addDietaryRestrictionIngredient(selectedIngredient);
            } else {
                //viewModel.deleteDietaryRestrictionIngredient(selectedIngredient);
            }

        }


    }
}