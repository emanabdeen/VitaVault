package com.example.insight.view;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class DietaryRestrictionCustomIngredientsViewHolder extends RecyclerView.ViewHolder {

    public TextView ingredient;
    ItemClickListener clickListener;
    ImageButton btnDelete;
    ImageButton btnEdit;


    public DietaryRestrictionCustomIngredientsViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);

        ingredient = itemView.findViewById(R.id.lblIngredient);
        this.clickListener = clickListener;

        //passing the clicked view and its position in the adapter
        btnEdit = itemView.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v,getAdapterPosition());
            }
        });


        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickDelete(v,getAdapterPosition());
            }
        });
    }

}
