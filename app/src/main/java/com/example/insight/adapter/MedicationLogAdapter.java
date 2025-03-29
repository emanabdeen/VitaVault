package com.example.insight.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;
import com.example.insight.model.MedicationLog;
import com.example.insight.view.EditItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicationLogAdapter extends RecyclerView.Adapter<MedicationLogAdapter.MedicationLogViewHolder> {

    private Context context;
    private List<MedicationLog> logList;
    private final EditItemClickListener listener;
    private String medicationId;

    public MedicationLogAdapter(Context context, List<MedicationLog> logList, String medicationId, EditItemClickListener listener) {
        this.context = context;
        this.logList = logList;
        this.medicationId = medicationId;
        this.listener = listener;
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

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(log.getTimestamp()); // Your string timestamp

            boolean is24Hour = android.text.format.DateFormat.is24HourFormat(context);
            String pattern = is24Hour ? "EEE, MMM d • HH:mm" : "EEE, MMM d • h:mm a";
            SimpleDateFormat outputFormat = new SimpleDateFormat(pattern, Locale.getDefault());

            String formattedTime = (date != null) ? outputFormat.format(date) : "Unknown";
            holder.timestamp.setText("Time: " + formattedTime);

            holder.timestamp.setText(formattedTime);
        } catch (Exception e) {
            holder.timestamp.setText(log.getTimestamp()); // Fallback
            e.printStackTrace(); // Optional: log error
        }

        // Set text color based on status
        String status = log.getStatus();

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNight = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        if(!isNight) {
            if (status.equalsIgnoreCase("dismissed")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.accent));
            } else if (status.equalsIgnoreCase("taken")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.graphLine_light));
            } else if (status.equalsIgnoreCase("missed")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.error));
            }
            holder.timestamp.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));
            holder.dosage.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        else{
            if (status.equalsIgnoreCase("dismissed")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.accent));
            } else if (status.equalsIgnoreCase("taken")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.takenLogStatus));
            } else if (status.equalsIgnoreCase("missed")) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.error));
            }
            holder.timestamp.setTextColor(ContextCompat.getColor(context, R.color.lighter_gray));
            holder.dosage.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClickDelete(v, position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClickEdit(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }
    public MedicationLog getLogAt(int pos) {
        return logList.get(pos);
    }

    public void removeLogAt(int position) {
        logList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateLogs(List<MedicationLog> logs) {
        this.logList = logs;
        notifyDataSetChanged();
    }
    public String getMedicationId() {
        return medicationId;
    }

    static class MedicationLogViewHolder extends RecyclerView.ViewHolder {

        TextView dosage, status, timestamp;
        ImageButton btnDelete, btnEdit;

        public MedicationLogViewHolder(@NonNull View itemView) {
            super(itemView);
            dosage = itemView.findViewById(R.id.textViewLogDosage);
            status = itemView.findViewById(R.id.textViewLogStatus);
            timestamp = itemView.findViewById(R.id.textViewLogTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);

        }
    }
}

