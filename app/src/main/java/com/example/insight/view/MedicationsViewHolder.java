package com.example.insight.view;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insight.R;

public class MedicationsViewHolder extends RecyclerView.ViewHolder {

    private TextView medicationName;
    private TextView medicationDosage;
    private TextView medicationDate;
    private MedicationItemClickListener clickListener;
    private ImageButton btnDelete;
    private ImageButton btnEdit;
    private ImageButton btnAlarm;

    public MedicationsViewHolder(@NonNull View itemView, MedicationItemClickListener clickListener) {
        super(itemView);
        medicationName = itemView.findViewById(R.id.medication_name_txt);
        medicationDosage = itemView.findViewById(R.id.medication_dosage_txt);
        this.clickListener = clickListener;
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnAlarm = itemView.findViewById(R.id.btnAlarm);

        // Passing the clicked view and its position in the adapter
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v, getAdapterPosition());
            }
        });

        // Passing the clicked view and its position in the adapter for delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickDelete(v, getAdapterPosition());
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v, getAdapterPosition());
            }
        });

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClickItem(v, getAdapterPosition());
            }
        });
    }

    // Getters & Setters
    public TextView getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(TextView medicationName) {
        this.medicationName = medicationName;
    }

    public TextView getMedicationDosage() {
        return medicationDosage;
    }

    public void setMedicationDosage(TextView medicationDosage) {
        this.medicationDosage = medicationDosage;
    }

    public TextView getMedicationDate() {
        return medicationDate;
    }

    public void setMedicationDate(TextView medicationDate) {
        this.medicationDate = medicationDate;
    }

    public ImageButton getBtnEdit() {
        return btnEdit;
    }

    public ImageButton getBtnDelete() {
        return btnDelete;
    }

    public ImageButton getBtnAlarm() {
        return btnAlarm;
    }
}
