package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.Medication;
import com.example.insight.view.ItemClickListener;
import com.example.insight.view.MedicationsViewHolder;

import java.util.List;

public class MedicationsListAdapter extends RecyclerView.Adapter<MedicationsViewHolder> {

    private Context context;
    private List<Medication> medications;
    private ItemClickListener clickListener;

    public MedicationsListAdapter(Context context, List<Medication> medications) {
        this.context = context;
        this.medications = medications;
    }

    public void setClickListener(ItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MedicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication_layout, parent, false);
        return new MedicationsViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationsViewHolder holder, int position) {
        Medication item = medications.get(position);
        holder.getMedicationName().setText(item.getName());
        holder.getMedicationDosage().setText(item.getDosage() + " " + item.getUnit());
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public void updateData(List<Medication> newMedications) {
        this.medications.clear();
        this.medications.addAll(newMedications);
        notifyDataSetChanged();
    }
}
