package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.OcrIngredient;
import com.example.insight.utility.StringHandler;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.OcrIngredientViewHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OcrIngredientListAdapter extends RecyclerView.Adapter<OcrIngredientViewHolder>{

    private Context context;
    private List<OcrIngredient> ocrIngredientsList;
    private ItemClickListener clickListener;

    // Gemini-flagged ingredients
    private Set<String> geminiFlaggedIngredients = new HashSet<>();
    private Map<String, String> geminiFlagReasons = new HashMap<>();


    public OcrIngredientListAdapter(Context context, List<OcrIngredient> ocrIngredientsList) {
        this.context = context;
        this.ocrIngredientsList = ocrIngredientsList;
    }


    public void setClickListener(ItemClickListener listener) {
        this.clickListener = listener;
    }
    public void setGeminiFlaggedIngredients(Set<String> flaggedIngredients) {
        this.geminiFlaggedIngredients = flaggedIngredients != null ? flaggedIngredients : new HashSet<>();
        notifyDataSetChanged(); // Refresh the list
    }

    @NonNull
    @Override
    public OcrIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ocringredient_layout, parent, false);
        return new OcrIngredientViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OcrIngredientViewHolder holder, int position) {
        OcrIngredient item = ocrIngredientsList.get(position);
        String ingredientName = item.getIngredientName().trim().toLowerCase();

        holder.getIngredientName().setText(item.getIngredientName());
        //holder.getIngredientMatchedStatus().setText(item.isDietaryRestrictionFlagged() ? "⚠️" : "");
        if (item.isDietaryRestrictionFlagged()){
            holder.getRestrictedIcon().setImageResource(R.drawable.icon_restrected);
            holder.getRestrictedIcon().setVisibility(View.VISIBLE);
        }

        if(item.getIngredientMatchedCategory()!=null){
            holder.getIngredientMatchedCategory().setText(" ("+item.getIngredientMatchedCategory()+")");
        }


        boolean staticFlagged = item.isDietaryRestrictionFlagged();
        boolean aiFlagged = geminiFlaggedIngredients.contains(ingredientName);

        // Show ⚠️ if either static or AI flagged and not a common restricted ingredient
        if (item.isSameNameAsCommonRestrictedIngredient() && !staticFlagged) {
            holder.getRestrictedIcon().setVisibility(View.GONE);
            holder.getIngredientMatchedCategory().setText(StringHandler.capitalizeFirstLetter(item.getIngredientName()));
            holder.getIngredientMatchedCategory().setVisibility(View.VISIBLE);
        }
        if (staticFlagged || aiFlagged) {
            if(staticFlagged){
                holder.getRestrictedIcon().setImageResource(R.drawable.icon_restrected);
                holder.getRestrictedIcon().setVisibility(View.VISIBLE);
            } else if (aiFlagged) {
                holder.getRestrictedIcon().setImageResource(R.drawable.icon_ai_restricted);
                holder.getRestrictedIcon().setVisibility(View.VISIBLE);
            }
            //holder.getIngredientMatchedStatus().setText("⚠️");
            //holder.getIngredientMatchedStatus().setVisibility(View.VISIBLE);

            String label;
            if (aiFlagged && geminiFlagReasons.containsKey(ingredientName)) {
                label = geminiFlagReasons.get(ingredientName);
            } else if (staticFlagged) {
                label = item.getIngredientMatchedCategory();
            } else {
                label = ""; // fallback, shouldn't usually happen
            }
            label = label.substring(0,1).toUpperCase() + label.substring(1).toLowerCase();
            holder.getIngredientMatchedCategory().setText(label);
            holder.getIngredientMatchedCategory().setVisibility(View.VISIBLE);
        } else {
            //holder.getIngredientMatchedStatus().setText("");
            //holder.getIngredientMatchedStatus().setVisibility(View.GONE);
            holder.getRestrictedIcon().setVisibility(View.GONE);
            holder.getIngredientMatchedCategory().setText("");
            holder.getIngredientMatchedCategory().setVisibility(View.GONE);
        }

        // Handle Add button visibility
        ImageButton button = holder.itemView.findViewById(R.id.btnAdd);
        if (item.isAddedAsCustom()) {
            button.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            button.setBackgroundResource(0);
            button.setImageResource(R.drawable.icon_check);
        } else {
            button.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            button.setImageResource(R.drawable.icon_add_restrict);
        }
        if (item.isDietaryRestrictionFlagged() || item.isSameNameAsCommonRestrictedIngredient()) {
            holder.itemView.findViewById(R.id.btnAdd).setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.findViewById(R.id.btnAdd).setVisibility(View.VISIBLE);
        }

        // Hide the button if the item is flagged (either by static or AI logic)
        if (staticFlagged || aiFlagged) {
            holder.itemView.findViewById(R.id.btnAdd).setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.findViewById(R.id.btnAdd).setVisibility(View.VISIBLE);
        }
    }



    @Override
    public int getItemCount() {
        return ocrIngredientsList.size();
    }

    public void updateData(List<OcrIngredient> newOcrIngredients) {
        this.ocrIngredientsList.clear();
        this.ocrIngredientsList.addAll(newOcrIngredients);
        notifyDataSetChanged();
    }

    public void setGeminiFlagReasons(Map<String, String> flagReasons) {
        this.geminiFlagReasons = flagReasons != null ? flagReasons : new HashMap<>();
        notifyDataSetChanged();
    }
}
