package com.example.insight.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class DietaryRestrictionCustomIngredientsViewHolder extends RecyclerView.ViewHolder {

    public TextView ingredient;
    ItemClickListener clickListener;


    public DietaryRestrictionCustomIngredientsViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);

        ingredient = itemView.findViewById(R.id.lblIngredient);
        this.clickListener = clickListener;


        //passing the clicked view and its position in the adapter
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v,getAdapterPosition());
            }
        });

    }

}
