package com.example.insight.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.view.DietaryRestrictionCustomIngredientsViewHolder;
import com.example.insight.view.DietaryRestrictionsPredefinedItemViewHolder;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.MultipleRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionPredefinedItemAdapter extends RecyclerView.Adapter<DietaryRestrictionsPredefinedItemViewHolder> {

    Context context;
    List<DietaryRestrictionIngredient> ingredientsList = new ArrayList<>();
    MultipleRecyclerViewItemClickListener clickListener;
    private String recyclerViewId;

    public DietaryRestrictionPredefinedItemAdapter(Context context, List<DietaryRestrictionIngredient> ingredientsList, MultipleRecyclerViewItemClickListener clickListener, String recyclerViewId) {
        this.context = context;
        this.ingredientsList = ingredientsList;
        this.clickListener = clickListener;
        this.recyclerViewId = recyclerViewId;

    }


    @NonNull
    @Override
    public DietaryRestrictionsPredefinedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predefined_dietary_restriction, parent, false);
        return new DietaryRestrictionsPredefinedItemViewHolder(itemView, clickListener, recyclerViewId);

    }

    @Override
    public void onBindViewHolder(@NonNull DietaryRestrictionsPredefinedItemViewHolder holder, int position) {
        DietaryRestrictionIngredient ingredient = ingredientsList.get(position);
        holder.ingredientCheckBox.setText(ingredient.getIngredientName());

        holder.itemView.setClickable(true);




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v,position, recyclerViewId,holder.ingredientCheckBox.isChecked());
            }
        });
    }

    // Call the listener with the position and RecyclerView ID


    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }
}
