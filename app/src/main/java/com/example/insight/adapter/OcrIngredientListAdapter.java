package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.OcrIngredient;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.OcrIngredientViewHolder;
import java.util.List;

public class OcrIngredientListAdapter extends RecyclerView.Adapter<OcrIngredientViewHolder>{

    private Context context;
    private List<OcrIngredient> ocrIngredientsList;
    private ItemClickListener clickListener;

    public OcrIngredientListAdapter(Context context, List<OcrIngredient> ocrIngredientsList) {
        this.context = context;
        this.ocrIngredientsList = ocrIngredientsList;
    }


    public void setClickListener(ItemClickListener listener) {
        this.clickListener = listener;
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
        holder.getIngredientName().setText(item.getIngredientName());
        holder.getIngredientMatchedStatus().setText(item.isDietaryRestrictionFlagged() ? "⚠️" : "");
        holder.getIngredientMatchedCategory().setText(item.getIngredientMatchedCategory());
        if (item.isDietaryRestrictionFlagged()) {
            holder.itemView.findViewById(R.id.btnAdd).setVisibility(View.INVISIBLE);
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
}
