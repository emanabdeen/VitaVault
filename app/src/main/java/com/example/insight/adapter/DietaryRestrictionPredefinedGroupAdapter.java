package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.view.DietaryRestrictionsGroupedPredefinedListViewHolder;

import java.util.List;
import java.util.Map;

public class DietaryRestrictionPredefinedGroupAdapter extends RecyclerView.Adapter<DietaryRestrictionsGroupedPredefinedListViewHolder> {


    Context context;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;


    DietaryRestrictionPredefinedItemAdapter itemAdapter;

    public DietaryRestrictionPredefinedGroupAdapter(Context context,Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> map) {
        this.context = context;
        this.restrictionsMap = map;
    }

    @NonNull
    @Override
    public DietaryRestrictionsGroupedPredefinedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

          View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grouped_item_predefined_dietary_restriction, parent,false);

        return new DietaryRestrictionsGroupedPredefinedListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull DietaryRestrictionsGroupedPredefinedListViewHolder holder, int position) {

       RestrictedIngredientsCategory category = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[position];
       String categoryName = category.getCategoryDescription();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.ingredientRecyclerView.setLayoutManager(layoutManager);


        holder.category.setText(categoryName);
        itemAdapter = new DietaryRestrictionPredefinedItemAdapter(restrictionsMap.get(category));
       holder.ingredientRecyclerView.setAdapter(itemAdapter);


    }

    @Override
    public int getItemCount() {
        return restrictionsMap.size();
    }
}
