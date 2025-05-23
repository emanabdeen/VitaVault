package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.view.DietaryRestrictionCustomIngredientsViewHolder;
import com.example.insight.view.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class DietaryRestrictionCustomIngredientAdapter extends RecyclerView.Adapter<DietaryRestrictionCustomIngredientsViewHolder> {

    Context context;
    List<DietaryRestrictionIngredient> ingredientsList = new ArrayList<>();
    ItemClickListener clickListener;

    public DietaryRestrictionCustomIngredientAdapter(Context context, List<DietaryRestrictionIngredient> ingredientsList) {
        this.context = context;
        this.ingredientsList = ingredientsList;

    }

    public void setClickListener(ItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DietaryRestrictionCustomIngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dietary_restriction_custom_ingredient,parent,false);
        return new DietaryRestrictionCustomIngredientsViewHolder(itemView,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DietaryRestrictionCustomIngredientsViewHolder holder, int position) {

        DietaryRestrictionIngredient ingredient = ingredientsList.get(position);

        holder.ingredient.setText(ingredient.getIngredientName());


    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public void updateIngredientList(List<DietaryRestrictionIngredient> ingredients) {
        this.ingredientsList.clear();
        this.ingredientsList.addAll(ingredients);
        notifyDataSetChanged();
    }

}
