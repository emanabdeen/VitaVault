package com.example.insight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.DietaryRestrictionIngredient;

import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.example.insight.view.DietaryRestrictionsPredefinedItemViewHolder;
import com.example.insight.view.ItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DietaryRestrictionPredefinedItemAdapter extends RecyclerView.Adapter<DietaryRestrictionsPredefinedItemViewHolder> {

    //Context context;
    List<CommonRestrictedIngredients> ingredientsList = new ArrayList<>();
    ItemClickListener clickListener;
    Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> restrictionsMap;

    public DietaryRestrictionPredefinedItemAdapter(List<CommonRestrictedIngredients> ingredientsList) {
       // this.context = context;
        this.ingredientsList = ingredientsList;

    }

    public void setClickListener(ItemClickListener clickListener){
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
        holder.ingredientCheckBox.setText(ingredient.GetIngredientDescription());
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }
}
