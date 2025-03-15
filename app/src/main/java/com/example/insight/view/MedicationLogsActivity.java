package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMedicationLogsBinding;

public class MedicationLogsActivity extends DrawerBaseActivity {

    private String medicationId;
    private String medicationName;
    private String dosage;

    // Fragments
    private MedicationLogsFragment historyFragment;
    private MedicationSettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMedicationLogsBinding binding = ActivityMedicationLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Get medication details from the Intent
        medicationId = getIntent().getStringExtra("medicationID");
        medicationName = getIntent().getStringExtra("medicationName");
        dosage = getIntent().getStringExtra("dosage");

        // 2. Optionally set a text title (if you have a TextView with ID textViewTitle)
        // binding.textViewTitle.setText(medicationName + " Logs");

        // 3. Create the fragments, passing the medicationId
        historyFragment = MedicationLogsFragment.newInstance(medicationId);
        settingsFragment = MedicationSettingsFragment.newInstance(medicationId);

        // 4. Show the History fragment by default
        replaceFragment(historyFragment);

        // 5. Set button listeners to swap fragments
        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(historyFragment);
            }
        });

        binding.btnReminderSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(settingsFragment);
            }
        });

        // 6. Set up the Add button to open the AddAlarmActivity
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicationLogsActivity.this, AddAlarmActivity.class);
                // Pass the medicationId so the new alarm is linked to the correct medication
                intent.putExtra("medicationID", medicationId);
                intent.putExtra("medicationName", medicationName);
                intent.putExtra("dosage", dosage);
                startActivity(intent);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragment)
                .commit();
    }
}
