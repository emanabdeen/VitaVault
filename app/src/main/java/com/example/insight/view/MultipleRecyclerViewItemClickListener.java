package com.example.insight.view;

import android.view.View;

public interface MultipleRecyclerViewItemClickListener {

    void onItemClick(View v,int position, String recyclerViewId, boolean isChecked);

}
