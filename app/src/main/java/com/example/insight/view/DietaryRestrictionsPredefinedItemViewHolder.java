package com.example.insight.view;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class DietaryRestrictionsPredefinedItemViewHolder extends RecyclerView.ViewHolder {


    public CheckBox ingredientCheckBox;
    ItemClickListener clickListener;

    public DietaryRestrictionsPredefinedItemViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);


        this.ingredientCheckBox = itemView.findViewById(R.id.ingredientCheckBox);
        this.clickListener = clickListener;
    }


}
