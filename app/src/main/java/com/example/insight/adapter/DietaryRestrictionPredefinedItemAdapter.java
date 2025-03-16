package com.example.insight.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.view.DietaryRestrictionCustomIngredientsViewHolder;
import com.example.insight.view.DietaryRestrictionsPredefinedItemViewHolder;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.MultipleRecyclerViewItemClickListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DietaryRestrictionPredefinedItemAdapter extends RecyclerView.Adapter<DietaryRestrictionsPredefinedItemViewHolder> {

    Context context;
    List<DietaryRestrictionIngredient> ingredientsList = new ArrayList<>();
    List<DietaryRestrictionIngredient> selectedIngredientsList = new ArrayList<>();

    MultipleRecyclerViewItemClickListener clickListener;
    private String recyclerViewId;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;

    public DietaryRestrictionPredefinedItemAdapter(Context context, List<DietaryRestrictionIngredient> ingredientsList, MultipleRecyclerViewItemClickListener clickListener, String recyclerViewId) {
        this.context = context;
        this.ingredientsList = ingredientsList;
        this.clickListener = clickListener;
        this.recyclerViewId = recyclerViewId;
    }
   public DietaryRestrictionPredefinedItemAdapter(Context context,  Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> ingredients, MultipleRecyclerViewItemClickListener clickListener) {
        this.context = context;
        this.restrictionsMap = ingredients;
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

    //bindIngredientLists(holder);

        DietaryRestrictionIngredient ingredient = ingredientsList.get(position);
        holder.ingredientCheckBox.setText(ingredient.getIngredientName());
        holder.ingredientCheckBox.setChecked(ingredient.getIsSelected());
        holder.setIngredientId(ingredient.getIngredientId());


        holder.itemView.setClickable(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, holder.getAdapterPosition(), recyclerViewId,holder.ingredientCheckBox.isChecked(), holder.getIngredientId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public void updateIngredientList(List<DietaryRestrictionIngredient> selectedIngredients) {

        for (DietaryRestrictionIngredient selectedIngredient : selectedIngredients) {

            Log.e("debug-Adapter","Selected: "+ selectedIngredient.getIngredientName());

                if(ingredientsList.stream().anyMatch(i -> i.getIngredientName() != null &&
                        selectedIngredient.getIngredientName() != null &&
                        i.getIngredientName().toLowerCase().contains(selectedIngredient.getIngredientName().toLowerCase()))) {


                    ingredientsList.forEach( ingredient -> {
                        selectedIngredients.stream()
                                .filter( selected -> selected.getIngredientName().equalsIgnoreCase(ingredient.getIngredientName()))
                                .findFirst()
                                .ifPresent(selected -> {
                                    ingredient.setIngredientId(selected.getIngredientId());
                                    ingredient.setSelected(selected.getIsSelected());
                                });
                    });

            }
        }
//
        notifyDataSetChanged();
    }

}
