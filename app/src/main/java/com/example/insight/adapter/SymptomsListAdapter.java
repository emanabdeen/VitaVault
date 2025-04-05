package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.Symptom;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.TimeValidator;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.SymptomsViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SymptomsListAdapter extends RecyclerView.Adapter<SymptomsViewHolder> {
    Context context;
    List<Symptom> symptoms = new ArrayList<>();;
    ItemClickListener clickListener;

    //constructors
    public SymptomsListAdapter() {
    }

    public SymptomsListAdapter(Context context, List<Symptom> symptoms) {
        this.context = context;
        this.symptoms = symptoms;
    }

    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    @NonNull
    @Override
    public SymptomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptom_layout,parent, false);
        return new SymptomsViewHolder(itemView, clickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull SymptomsViewHolder holder, int position) {
        Symptom item = symptoms.get(position);

        String endTimeStr= StringHandler.defaultIfNull(TimeValidator.LocalTimeToString(item.getEndTime().orElse(null)));
        if (endTimeStr.isEmpty()){
            endTimeStr= "...";
        }

        String dateTimeStr = DateValidator.LocalDateToString(item.getRecordDate()) +" ("+ TimeValidator.LocalTimeToString(item.getStartTime())+" - "+ endTimeStr+") "; // 10-10-2025 (13:30)
        String levelValueStr = item.getSymptomLevel();
        String descriptionStr = item.getSymptomDescription();

        holder.getDateTime().setText(dateTimeStr);
        holder.getValue().setText(levelValueStr);
        holder.getDescription().setText(descriptionStr);
    }

    // Method to update data dynamically
    public void updateData(List<Symptom> newSymptoms) {
        if (newSymptoms != null) {
            this.symptoms.clear();
            this.symptoms.addAll(newSymptoms);
        } else {
            this.symptoms.clear(); // Clear existing data if new data is null
        }
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }
}
