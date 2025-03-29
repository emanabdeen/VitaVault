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
    private MedicationLogsFragment logsFragment;
    private MedicationAlarmsFragment alarmsFragment;

    private ActivityResultLauncher<Intent> alarmActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMedicationLogsBinding binding = ActivityMedicationLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        //this gets rid of unnecessary firebase calls
        alarmActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (alarmsFragment != null) {
                            alarmsFragment.refreshAlarms(); // let the fragment handle the logic
                        }
                    }
                }
        );

        // Get medication details from the Intent
        medicationId = getIntent().getStringExtra("medicationID");
        medicationName = getIntent().getStringExtra("medicationName");
        dosage = getIntent().getStringExtra("dosage");
        boolean showAlarms = getIntent().getBooleanExtra("showAlarms", false);


        // Optionally set a text title
         binding.textViewTitle.setText(medicationName);

        // Create the fragments, passing the medicationId
        logsFragment = MedicationLogsFragment.newInstance(medicationId);
        alarmsFragment = MedicationAlarmsFragment.newInstance(medicationId);

        if (showAlarms) {
            replaceFragment(alarmsFragment);
            binding.btnAddLog.setVisibility(View.GONE);
            binding.btnAddAlarm.setVisibility(View.VISIBLE);
            allocateActivityTitle("Alarms");
        } else {
            replaceFragment(logsFragment);
            binding.btnAddAlarm.setVisibility(View.GONE);
            binding.btnAddLog.setVisibility(View.VISIBLE);
            allocateActivityTitle("Logs");
        }


        // Set button listeners to swap fragments
        binding.btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaceFragment(logsFragment);
                binding.btnAddAlarm.setVisibility(View.GONE);
                binding.btnAddLog.setVisibility(View.VISIBLE);
                allocateActivityTitle("Logs");

                binding.image.setImageResource(R.drawable.ic_medical_record);
            }
        });

        binding.btnAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaceFragment(alarmsFragment);
                binding.btnAddLog.setVisibility(View.GONE);
                binding.btnAddAlarm.setVisibility(View.VISIBLE);
                allocateActivityTitle("Alarms");

                binding.image.setImageResource(R.drawable.ic_alarm_clock);
            }
        });

        binding.btnAddLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo add log intent to add a log
                Intent intent = new Intent(MedicationLogsActivity.this, AddLogActivity.class);
                intent.putExtra("medicationID", medicationId);
                intent.putExtra("medicationName", medicationName);
                intent.putExtra("dosage", dosage);
                startActivity(intent);
            }
        });

        // Set up the Add button to open the AddAlarmActivity
        binding.btnAddAlarm.setOnClickListener(new View.OnClickListener() {
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
