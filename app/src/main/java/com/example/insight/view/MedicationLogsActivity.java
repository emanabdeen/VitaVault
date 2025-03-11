package com.example.insight.view;

import android.os.Bundle;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMedicationLogsBinding;

public class MedicationLogsActivity extends DrawerBaseActivity {

    private String medicationId;
    private String medicationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMedicationLogsBinding binding = ActivityMedicationLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get medication details from intent
        medicationId = getIntent().getStringExtra("medicationID");
        medicationName = getIntent().getStringExtra("medicationName");

        // Set Header Title
        binding.textViewLogsTitle.setText(medicationName + " Logs");

        // Pass medicationId to fragment
        MedicationLogsFragment fragment = MedicationLogsFragment.newInstance(medicationId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerLogs, fragment)
                .commit();
    }
}
