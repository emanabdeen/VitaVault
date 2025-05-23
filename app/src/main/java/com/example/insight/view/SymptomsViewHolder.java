package com.example.insight.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class SymptomsViewHolder extends RecyclerView.ViewHolder  {
    TextView dateTime;
    TextView value;
    TextView description;
    ItemClickListener clickListener;
    ImageButton btnDelete;
    ImageButton btnEdit;

    public SymptomsViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);
        dateTime = itemView.findViewById(R.id.date_time_txt);
        value = itemView.findViewById(R.id.value_txt);
        description = itemView.findViewById(R.id.notes_txt);
        this.clickListener= clickListener;
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        //passing the clicked view and its position in the adapter
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v,getAdapterPosition());
            }
        });

        //passing the clicked view and its position in the adapter for delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickDelete(v,getAdapterPosition());
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v,getAdapterPosition());
            }
        });
    }

    //Getters & Setters

    public TextView getDateTime() {
        return dateTime;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public void setDateTime(TextView dateTime) {
        this.dateTime = dateTime;
    }

    public TextView getValue() {
        return value;
    }

    public void setValue(TextView value) {
        this.value = value;
    }
}
