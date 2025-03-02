package com.example.insight.view;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.utility.CommonRestrictedIngredients;

public class DietaryRestrictionsPredefinedItemViewHolder extends RecyclerView.ViewHolder {


    public CheckBox ingredientCheckBox;
    MultipleRecyclerViewItemClickListener clickListener;
    private String recyclerViewId;

    public DietaryRestrictionsPredefinedItemViewHolder(@NonNull View itemView, MultipleRecyclerViewItemClickListener clickListener, String recyclerViewId) {
        super(itemView);

        this.ingredientCheckBox = itemView.findViewById(R.id.ingredientCheckBox);
        this.clickListener = clickListener;
        this.recyclerViewId = recyclerViewId;



        ingredientCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v,getAdapterPosition(),recyclerViewId);
            }
        });

    }


}
