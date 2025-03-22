package com.example.insight.view;

import android.view.View;

public interface EditItemClickListener {
    void OnClickEdit(View v, int pos);
    void OnClickItem(View v, int pos);
    void OnClickDelete(View v, int pos);
}
