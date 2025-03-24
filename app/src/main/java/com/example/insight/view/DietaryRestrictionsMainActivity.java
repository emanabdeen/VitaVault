package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.DietaryRestrictionPredefinedItemAdapter;
import com.example.insight.databinding.ActivityDietaryRestrictionsMainBinding;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.viewmodel.DietaryRestrictionIngredientViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DietaryRestrictionsMainActivity extends DrawerBaseActivity implements MultipleRecyclerViewItemClickListener {

    ActivityDietaryRestrictionsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;
    private DietaryRestrictionIngredientViewModel viewModel;
    //private List<DietaryRestrictionIngredient> selectedIngredientList;
    DietaryRestrictionPredefinedItemAdapter diaryAdapter, glutenAdapter, porkAdapter, shellfishAdapter, meatAdapter,nutsAdapter, othersAdapter ;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //selectedIngredientList = new ArrayList<>();

        if (user == null) {
            finish();
            startActivity(new Intent(DietaryRestrictionsMainActivity.this, Login.class));
        }

        restrictionsMap = CommonRestrictedIngredients.GetAllIngredientsWithCategory();

        viewModel = new ViewModelProvider(this).get(DietaryRestrictionIngredientViewModel.class);
        viewModel.getPredefinedDietaryRestrictionIngredients();



        int i = 0;
        for (Map.Entry<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> entry : restrictionsMap.entrySet()) {

            try {

                RestrictedIngredientsCategory ingredientsCategory = entry.getKey();
                List<CommonRestrictedIngredients> commonRestrictedIngredientsList = entry.getValue();

                /////////

                Log.i("debug-main", ingredientsCategory.getCategoryDescription());
                List<DietaryRestrictionIngredient> ingredientListBean = new ArrayList<>();

                for (CommonRestrictedIngredients ingredient : commonRestrictedIngredientsList) {

                    Log.i("debug-main", ingredient.getIngredientDescription());

                    DietaryRestrictionIngredient newIngredient = new DietaryRestrictionIngredient();
                    newIngredient.setIngredientName(ingredient.getIngredientDescription());
                    newIngredient.setIngredientCategory(ingredientsCategory.getCategoryDescription());
                    ingredientListBean.add(newIngredient);
                }

            if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.DAIRY.getCategoryDescription())){

                Log.e("debug-main", ingredientsCategory.getCategoryDescription());

                //parse Enum to Bean

                String diaryGroupList = String.valueOf(binding.groupList1.getResources().getResourceEntryName(binding.groupList1.getId()));

                viewModel.setDiaryIngredientList(ingredientListBean);

                diaryAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, diaryGroupList);

                binding.group1.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList1.setAdapter(diaryAdapter);
                binding.groupList1.setLayoutManager(new LinearLayoutManager(this));

            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.GLUTEN.getCategoryDescription())){

                String glutenGroupList = String.valueOf(binding.groupList2.getResources().getResourceEntryName(binding.groupList2.getId()));

                viewModel.setGlutenIngredientList(ingredientListBean);
                glutenAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, glutenGroupList);

                binding.group2.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList2.setAdapter(glutenAdapter);
                binding.groupList2.setLayoutManager(new LinearLayoutManager(this));


            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.PORK.getCategoryDescription())){

                String porkGroupList = String.valueOf(binding.groupList3.getResources().getResourceEntryName(binding.groupList3.getId()));

                viewModel.setPorkIngredientList(ingredientListBean);
                porkAdapter =  new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, porkGroupList);

                binding.group3.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList3.setAdapter(porkAdapter);
                binding.groupList3.setLayoutManager(new LinearLayoutManager(this));

            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.SHELLFISH.getCategoryDescription())){

                String shellfishGroupList = String.valueOf(binding.groupList4.getResources().getResourceEntryName(binding.groupList4.getId()));

                viewModel.setShellfishIngredientList(ingredientListBean);
                shellfishAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, shellfishGroupList);

                binding.group4.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList4.setAdapter(shellfishAdapter);
                binding.groupList4.setLayoutManager(new LinearLayoutManager(this));

            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.MEATS.getCategoryDescription())){

                String meatGroupList = String.valueOf(binding.groupList5.getResources().getResourceEntryName(binding.groupList5.getId()));

                viewModel.setMeatsIngredientList(ingredientListBean);
                meatAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, meatGroupList);

                binding.group5.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList5.setAdapter(meatAdapter);
                binding.groupList5.setLayoutManager(new LinearLayoutManager(this));


            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.NUTS.getCategoryDescription())){


                String nutsGroupList = String.valueOf(binding.groupList6.getResources().getResourceEntryName(binding.groupList6.getId()));


                viewModel.setNutsIngredientList(ingredientListBean);
                nutsAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, nutsGroupList);

                binding.group6.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList6.setAdapter(nutsAdapter);
                binding.groupList6.setLayoutManager(new LinearLayoutManager(this));


            }else if(ingredientsCategory.getCategoryDescription().equalsIgnoreCase(RestrictedIngredientsCategory.OTHER.getCategoryDescription())){


                String otherGroupList = String.valueOf(binding.groupList7.getResources().getResourceEntryName(binding.groupList7.getId()));

                viewModel.setOtherIngredientList(ingredientListBean);
                othersAdapter = new DietaryRestrictionPredefinedItemAdapter(this, ingredientListBean, this, otherGroupList);

                binding.group7.setText(ingredientsCategory.getCategoryDescription());
                binding.groupList7.setAdapter(othersAdapter);
                binding.groupList7.setLayoutManager(new LinearLayoutManager(this));

            }else{
                Log.e("debug","Invalid Category: "+ingredientsCategory.getCategoryDescription());
            }


            }catch (Exception e){
                Log.e("debug", e.getMessage());
            }
            i++;
        }

        fetchIngredientList();

        /*
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
              }
          });
          */


            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentObj = new Intent(getApplicationContext(), DietaryRestrictionsAddCustomActivity.class);
                    startActivity(intentObj);

                }
            });

        }



    @Override
    public void onItemClick(View v, int position, String recyclerViewId, boolean isChecked, String ingredientId) {

        //based on the list, identifies the category in the map

        String[] categoryPosition = recyclerViewId.split("groupList");

        if (categoryPosition.length > 1) {

            //decrease 1 as map index starts in 0, and groupList starts as 1.
            int mapCategoryPosition = Integer.parseInt(categoryPosition[1])-1;
            RestrictedIngredientsCategory selectedCategory = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[mapCategoryPosition];
            List<CommonRestrictedIngredients> clickedIngredientList = restrictionsMap.get(selectedCategory);
            CommonRestrictedIngredients clickedItem = clickedIngredientList.get(position);


            if (isChecked) {
                DietaryRestrictionIngredient selectedIngredient = new DietaryRestrictionIngredient(clickedItem.getIngredientDescription(),selectedCategory.getCategoryDescription() );
                viewModel.addDietaryRestrictionIngredient(selectedIngredient);

            } else {
                viewModel.deleteDietaryRestrictionIngredient(ingredientId);
            }

            fetchIngredientList();

        }

    }

    private void fetchIngredientList(){

        //selectedIngredientList.clear();
        viewModel.getPredefinedDietaryRestrictionIngredients();


        viewModel.getDiaryIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                diaryAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });


        viewModel.getGlutenIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                glutenAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });

        viewModel.getPorkIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                porkAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });


        viewModel.getShellfishIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                shellfishAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });


        viewModel.getMeatsIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                meatAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });


        viewModel.getNutsIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                nutsAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });


        viewModel.getOtherIngredientLiveData().observe(this, ingredientData -> {
            if (ingredientData != null && !ingredientData.isEmpty()) {

                //selectedIngredientList.addAll(ingredientData.stream().filter(i -> i.getIsSelected() == true).collect(Collectors.toList()));
                othersAdapter.updateIngredientList(ingredientData);

                Log.d("debug-main", " DIARY ingredientList count>> " + ingredientData.size());
            }
            else {
                Log.d("debug-main", " DAIRY ingredientList is null or empty.");
            }
        });







    }

}