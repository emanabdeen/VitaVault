package com.example.insight.view;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class OcrIngredientViewHolder extends RecyclerView.ViewHolder {

    private TextView ingredientName;

    //private TextView ingredientMatchedStatus;
    private TextView ingredientMatchedCategory;
    private ItemClickListener clickListener;
    private ImageButton restrictedIcon;
    private ImageButton btnAdd;

    public OcrIngredientViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        //ingredientMatchedStatus = itemView.findViewById(R.id.matchedStatus);
        ingredientMatchedCategory = itemView.findViewById(R.id.matchedCategory);
        this.clickListener = clickListener;
        btnAdd = itemView.findViewById(R.id.btnAdd);
        restrictedIcon=itemView.findViewById(R.id.imgStatus);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v, getAdapterPosition());
            }
        });
    }


    public TextView getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(TextView ingredientName) {
        this.ingredientName = ingredientName;
    }

    /*public TextView getIngredientMatchedStatus() {
        return ingredientMatchedStatus;
    }*/

    /*public void setIngredientMatchedStatus(TextView ingredientMatchedStatus) {
        this.ingredientMatchedStatus = ingredientMatchedStatus;
    }*/

    public void setRestrictedIcon(ImageButton restrictedIcon) {
        this.restrictedIcon = restrictedIcon;
    }

    public ImageButton getRestrictedIcon() {
        return restrictedIcon;
    }

    public TextView getIngredientMatchedCategory() {
        return ingredientMatchedCategory;
    }

    public void setIngredientMatchedCategory(TextView ingredientMatchedCategory) {
        this.ingredientMatchedCategory = ingredientMatchedCategory;
    }

    public void updateButtonIconAfterAdd() {

    }
}
