package com.example.insight.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.databinding.ItemMedicationAlarmLayoutBinding;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.view.EditItemClickListener;

import java.util.List;

public class MedicationAlarmAdapter extends RecyclerView.Adapter<MedicationAlarmAdapter.AlarmViewHolder> {

    private final Context context;
    private List<MedicationAlarm> alarmList;
    private final EditItemClickListener listener;

    public MedicationAlarmAdapter(Context context, List<MedicationAlarm> alarmList, EditItemClickListener listener) {
        this.context = context;
        this.alarmList = alarmList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicationAlarmLayoutBinding binding =
                ItemMedicationAlarmLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AlarmViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        MedicationAlarm alarm = alarmList.get(position);

        // Bind alarm details from the model
        holder.binding.alarmDayTxt.setText(alarm.getDay());
        holder.binding.alarmTimeTxt.setText(alarm.getTime());

        // Use the provided EditItemClickListener for edit and delete actions.
        holder.binding.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClickEdit(v, position);
            }
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClickDelete(v, position);
            }
        });

        // Optionally handle item clicks
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnClickItem(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAlarms(List<MedicationAlarm> alarms) {
        this.alarmList = alarms;
        notifyDataSetChanged();
    }

    public MedicationAlarm getAlarmAt(int pos) {
        return alarmList.get(pos);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        public final ItemMedicationAlarmLayoutBinding binding;

        public AlarmViewHolder(ItemMedicationAlarmLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
