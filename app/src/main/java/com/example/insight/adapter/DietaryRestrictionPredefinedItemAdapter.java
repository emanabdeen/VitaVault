package com.example.insight.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.view.DietaryRestrictionsPredefinedItemViewHolder;
import com.example.insight.view.GroupedRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionPredefinedItemAdapter extends RecyclerView.Adapter<DietaryRestrictionsPredefinedItemViewHolder> {

    //Context context;
    private List<CommonRestrictedIngredients> ingredientsList = new ArrayList<>();
    private GroupedRecyclerViewItemClickListener clickListener;


    public DietaryRestrictionPredefinedItemAdapter(List<CommonRestrictedIngredients> ingredientsList, GroupedRecyclerViewItemClickListener clickListener) {
       // this.context = context;
        this.ingredientsList = ingredientsList;
        this.clickListener = clickListener;

    }




    @NonNull
    @Override
    public DietaryRestrictionsPredefinedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predefined_dietary_restriction,parent,false);

        return new DietaryRestrictionsPredefinedItemViewHolder(itemView,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DietaryRestrictionsPredefinedItemViewHolder holder, int position) {

        CommonRestrictedIngredients ingredient = ingredientsList.get(position);

        holder.ingredientCheckBox.setText(ingredient.getIngredientDescription());

        holder.ingredientCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(holder.getAdapterPosition(), position);
                }
                Log.e("INNER-PredefItem",ingredient.getIngredientDescription());
            }
        });

    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }
}
