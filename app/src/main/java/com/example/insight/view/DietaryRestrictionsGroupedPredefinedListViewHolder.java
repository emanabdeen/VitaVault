/*
package com.example.insight.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.adapter.DietaryRestrictionPredefinedItemAdapter;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.google.android.gms.common.internal.service.Common;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionsGroupedPredefinedListViewHolder extends RecyclerView.ViewHolder {

    public TextView category;
    public RecyclerView ingredientRecyclerView;
    private DietaryRestrictionPredefinedItemAdapter itemAdapter;
    private GroupedRecyclerViewItemClickListener clickListener;
    private List<Object> flattenedList;

    public DietaryRestrictionsGroupedPredefinedListViewHolder(@NonNull View itemView, GroupedRecyclerViewItemClickListener clickListener, List<Object> flatList) {
        super(itemView);

        this.category = itemView.findViewById(R.id.categoryTitle);
        this.ingredientRecyclerView = itemView.findViewById(R.id.groupedItemList);
        this.flattenedList = flatList;
        this.clickListener = clickListener;

    }

    public void bindCategory(RestrictedIngredientsCategory ingredientCategory , int outerPos) {
        category.setText(ingredientCategory.getCategoryDescription());

        // Get the items for this category and bind the inner adapter
        List<CommonRestrictedIngredients> items = getItemsForCategory(outerPos);

        DietaryRestrictionPredefinedItemAdapter innerAdapter = new DietaryRestrictionPredefinedItemAdapter(items, clickListener, outerPos);  // Pass outerPos to InnerAdapter
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        ingredientRecyclerView.setAdapter(innerAdapter);
    }

    private List<CommonRestrictedIngredients> getItemsForCategory(int outerPos) {
        List<CommonRestrictedIngredients> ingredients = new ArrayList<>();
        int categoryIndex = outerPos;

        // Add items for the current category until the next category
        for (int i = categoryIndex + 1; i < flattenedList.size(); i++) {
            Object obj = flattenedList.get(i);
            if (obj instanceof CommonRestrictedIngredients) {
                ingredients.add((CommonRestrictedIngredients) obj);  // Add item
            } else if (obj instanceof RestrictedIngredientsCategory) {
                break;  // Stop at the next Category
            }
        }
        return ingredients;
    }

    public void bindItem(CommonRestrictedIngredients item, int position) {
    }


}
*/