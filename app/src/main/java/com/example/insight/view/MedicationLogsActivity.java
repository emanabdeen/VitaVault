package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private ActivityResultLauncher<Intent> alarmActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMedicationLogsBinding binding = ActivityMedicationLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        alarmActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (settingsFragment != null) {
                            settingsFragment.refreshAlarms(); // 👈 let the fragment handle the logic
                        }
                    }
                }
        );

        // Get medication details from the Intent
        medicationId = getIntent().getStringExtra("medicationID");
        medicationName = getIntent().getStringExtra("medicationName");
        dosage = getIntent().getStringExtra("dosage");


        // Optionally set a text title
         binding.textViewTitle.setText(medicationName);

        // Create the fragments, passing the medicationId
        historyFragment = MedicationLogsFragment.newInstance(medicationId);
        settingsFragment = MedicationSettingsFragment.newInstance(medicationId);

        // Show the History fragment by default
        replaceFragment(historyFragment);


        // Set button listeners to swap fragments
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

        // Set up the Add button to open the AddAlarmActivity
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicationLogsActivity.this, AddAlarmActivity.class);
                // Pass the medicationId so the new alarm is linked to the correct medication
                intent.putExtra("medicationID", medicationId);
                intent.putExtra("medicationName", medicationName);
                intent.putExtra("dosage", dosage);
                alarmActivityLauncher.launch(intent);
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
