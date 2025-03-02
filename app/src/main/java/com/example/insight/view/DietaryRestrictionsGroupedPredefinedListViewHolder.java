package com.example.insight.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.adapter.DietaryRestrictionPredefinedItemAdapter;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionsGroupedPredefinedListViewHolder extends RecyclerView.ViewHolder {

    public TextView category;
    public RecyclerView ingredientRecyclerView;
    private DietaryRestrictionPredefinedItemAdapter itemAdapter;
    private GroupedRecyclerViewItemClickListener clickListener;

    public DietaryRestrictionsGroupedPredefinedListViewHolder(@NonNull View itemView, GroupedRecyclerViewItemClickListener clickListener) {
        super(itemView);

        this.category = itemView.findViewById(R.id.categoryTitle);
        this.ingredientRecyclerView = itemView.findViewById(R.id.groupedItemList);
        this.clickListener = clickListener;

    }


}
