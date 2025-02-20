package com.example.insight.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.insight.R;
import com.example.insight.databinding.ActivityModeBinding;
import com.example.insight.databinding.ActivitySymptomBinding;

public class ModeActivity extends DrawerBaseActivity {

    ActivityModeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        allocateActivityTitle("Mode");

    }
}