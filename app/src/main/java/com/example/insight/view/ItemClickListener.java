package com.example.insight.view;

import android.view.View;

public interface ItemClickListener {
    void OnClickItem(View v, int pos);
    void OnClickDelete(View v, int pos);
}
