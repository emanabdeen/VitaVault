package com.example.insight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.MedicationLog;

import java.util.List;

public class MedicationLogAdapter extends RecyclerView.Adapter<MedicationLogAdapter.MedicationLogViewHolder> {

    private Context context;
    private List<MedicationLog> logList;

    public MedicationLogAdapter(Context context, List<MedicationLog> logList) {
        this.context = context;
        this.logList = logList;
    }

    @NonNull
    @Override
    public MedicationLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication_log_layout, parent, false);
        return new MedicationLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationLogViewHolder holder, int position) {
        MedicationLog log = logList.get(position);
        holder.dosage.setText("Dosage: " + log.getDosage());
        holder.status.setText("Status: " + log.getStatus());
        holder.timestamp.setText("Time: " + log.getTimestamp());

        // Set text color based on status
        String status = log.getStatus();
        if (status.equalsIgnoreCase("dismissed")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.accent));
        } else if (status.equalsIgnoreCase("taken")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.graphLine_light));
        } else if (status.equalsIgnoreCase("missed")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.error));
        }
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public void updateLogs(List<MedicationLog> logs) {
        this.logList = logs;
        notifyDataSetChanged();
    }

    static class MedicationLogViewHolder extends RecyclerView.ViewHolder {

        TextView dosage, status, timestamp;

        public MedicationLogViewHolder(@NonNull View itemView) {
            super(itemView);
            dosage = itemView.findViewById(R.id.textViewLogDosage);
            status = itemView.findViewById(R.id.textViewLogStatus);
            timestamp = itemView.findViewById(R.id.textViewLogTimestamp);
        }
    }
}

