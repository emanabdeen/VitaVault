package com.example.insight.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.TimeValidator;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.VitalsViewHolder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VitalsListAdapter extends RecyclerView.Adapter<VitalsViewHolder>{

    Context context;
    List<Vital> vitals = new ArrayList<>();;
    ItemClickListener clickListener;

    //constructors
    public VitalsListAdapter() {
    }

    public VitalsListAdapter(Context context, List<Vital> vitals) {
        this.context = context;
        this.vitals = vitals;
    }

    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    @NonNull
    @Override
    public VitalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vital_layout,parent, false);
        return new VitalsViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VitalsViewHolder holder, int position) {
        Vital item = vitals.get(position);

        String dateTimeStr = DateValidator.LocalDateToString(item.getRecordDate()) +" ("+ TimeValidator.LocalTimeToString(item.getRecordTime()) +")"; // 10-10-2025 (13:30)
        String valueStr;

        //if measurement2 empty
        if (TextUtils.isEmpty(item.getMeasurement2())){
            valueStr = item.getMeasurement1()+" "+ item.getUnit(); // 50 Kg
        }else{
            valueStr = item.getMeasurement1()+" "+ item.getUnit()+" - "+item.getMeasurement2()+" "+ item.getUnit();  //120 mmHg - 80 mmHg
        }

        holder.getDateTime().setText(dateTimeStr);
        holder.getValue().setText(valueStr);

    }

    // Method to update data dynamically
    public void updateData(List<Vital> newVitals) {
        if (newVitals != null) {
            this.vitals.clear();
            this.vitals.addAll(newVitals);
        } else {
            this.vitals.clear(); // Clear existing data if new data is null
        }
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    @Override
    public int getItemCount() {
        return vitals.size();
    }
}
