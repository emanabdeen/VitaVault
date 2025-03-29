package com.example.insight.view;

import android.view.View;

public interface MedicationItemClickListener {
    void OnClickEdit(View v, int pos);
    void OnClickItem(View v, int pos);
    void OnClickDelete(View v, int pos);
    void OnClickAlarm(View v, int pos);
}
