package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.insight.R;
import com.example.insight.model.Medication;
import com.example.insight.view.EditItemClickListener;
import com.example.insight.view.MedicationItemClickListener;
import com.example.insight.view.MedicationsViewHolder;

import java.util.List;

public class MedicationsListAdapter extends RecyclerView.Adapter<MedicationsViewHolder> {

    private final Context context;
    private List<Medication> medications;
    private final MedicationItemClickListener clickListener;

    // Pass clickListener via constructor directly (cleaner approach)
    public MedicationsListAdapter(Context context, List<Medication> medications, MedicationItemClickListener clickListener) {
        this.context = context;
        this.medications = medications;
        this.clickListener = clickListener;
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

        // Main card click -> open Logs
        holder.itemView.setOnClickListener(v -> clickListener.OnClickItem(v, position));

        // Edit button click -> open MedicationDetails
        holder.getBtnEdit().setOnClickListener(v -> clickListener.OnClickEdit(v, position));

        // Delete button click
        holder.getBtnDelete().setOnClickListener(v -> clickListener.OnClickDelete(v, position));

        // Alarm button click
        holder.getBtnAlarm().setOnClickListener(v -> clickListener.OnClickAlarm(v, position));
    }

    @Override
    public int getItemCount() {
        return medications != null ? medications.size() : 0;
    }

    // Clean way to update dataset
    public void updateData(List<Medication> newMedications) {
        this.medications = newMedications;
        notifyDataSetChanged();
    }
}
