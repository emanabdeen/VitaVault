package com.example.insight.adapter;

import android.content.Context;
import android.util.Log;
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
import com.example.insight.view.GroupedRecyclerViewItemClickListener;
import com.example.insight.view.ItemClickListener;

import java.util.List;
import java.util.Map;

public class DietaryRestrictionPredefinedGroupAdapter extends RecyclerView.Adapter<DietaryRestrictionsGroupedPredefinedListViewHolder> {


    Context context;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;
    DietaryRestrictionPredefinedItemAdapter itemAdapter;
    private GroupedRecyclerViewItemClickListener clickListener;

    public DietaryRestrictionPredefinedGroupAdapter(Context context,Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> map, GroupedRecyclerViewItemClickListener clickListener) {
        this.context = context;
        this.restrictionsMap = map;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DietaryRestrictionsGroupedPredefinedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

          View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grouped_item_predefined_dietary_restriction, parent,false);

        return new DietaryRestrictionsGroupedPredefinedListViewHolder(itemView, clickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull DietaryRestrictionsGroupedPredefinedListViewHolder holder, int position) {

       RestrictedIngredientsCategory category = (RestrictedIngredientsCategory) restrictionsMap.keySet().toArray()[position];
       String categoryName = category.getCategoryDescription();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.ingredientRecyclerView.setLayoutManager(layoutManager);


        holder.category.setText(categoryName);



        //set inner adapter
        itemAdapter = new DietaryRestrictionPredefinedItemAdapter(restrictionsMap.get(category), (outerPos, innerPos) -> {
            clickListener.onItemClick(outerPos, innerPos);
        });

        holder.ingredientRecyclerView.setAdapter(itemAdapter);

    }

    @Override
    public int getItemCount() {
        return restrictionsMap.size();
    }
}
