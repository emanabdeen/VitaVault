package com.example.insight.view;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityMedicationDetailsBinding;
import com.example.insight.model.Medication;
import com.example.insight.viewmodel.MedicationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MedicationDetails extends DrawerBaseActivity {
    private ActivityMedicationDetailsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MedicationViewModel viewModel;
    private String pageFunction;
    private String medicationId;
    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicationDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Medication Details");

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        setupUnitSpinner();

        // Handle Intent Data (Editing or Creating New)
        Intent intent = getIntent();
        binding.textViewTitle.setText(intent.getStringExtra("title"));

        if (!TextUtils.isEmpty(intent.getStringExtra("medicationID"))) {
            pageFunction = "editMedication";
            medicationId = intent.getStringExtra("medicationID");

            viewModel.getMedications(true);
            viewModel.getMedicationsData().observe(this, medications -> {
                if (medications == null) {
                    Log.d("MedicationDetails", "Waiting for Firestore data...");
                    return;
                }
                for (Medication m : medications) {
                    if (m.getMedicationId().equals(medicationId)) {
                        medication = m;
                        Log.d("MedicationDetails", "Loaded medication: " + m.getName());
                        populateFields(m);
                        break;
                    }
                }
            });

            // In edit mode, enable the Manage Alarms button and hide the warning.
            binding.btnManageAlarms.setEnabled(true);
            binding.textViewAlarmWarning.setVisibility(View.GONE);
            binding.btnManageAlarms.setOnClickListener(v -> {
                Intent alarmIntent = new Intent(MedicationDetails.this, AddAlarmActivity.class);
                alarmIntent.putExtra("medicationID", medicationId);
                alarmIntent.putExtra("medicationName", medication.getName());
                alarmIntent.putExtra("dosage", medication.getDosage() + " " + medication.getUnit());
                startActivity(alarmIntent);
            });
        } else {
            pageFunction = "createMedication";
            // Disable Manage Alarms button until medication is saved.
            binding.btnManageAlarms.setEnabled(false);
            binding.textViewAlarmWarning.setVisibility(View.VISIBLE);
            binding.textViewAlarmWarning.setText("Need to add medication first");
        }

        // Save Button Click Event
        binding.btnSave.setOnClickListener(v -> {
            try {
                if (pageFunction.equals("createMedication")) {
                    createNewMedication();
                } else {
                    editMedication();
                }
            } catch (Exception e) {
                Log.e("MedicationDetails", "Error saving medication: " + e.getMessage());
            }
        });
    }

    private void setupUnitSpinner() {
        List<String> units = Arrays.asList("mg", "mL", "tablets", "puffs", "drops");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        binding.spinnerUnit.setAdapter(adapter);
    }

    private void populateFields(Medication medication) {
        binding.editTextMedicationName.setText(medication.getName());
        binding.editTextDosage.setText(medication.getDosage());
        String unit = medication.getUnit();
        if (unit != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinnerUnit.getAdapter();
            int position = adapter.getPosition(unit);
            binding.spinnerUnit.setSelection(position);
        }
    }

    private void createNewMedication() {
        String name = binding.editTextMedicationName.getText().toString();
        String dosage = binding.editTextDosage.getText().toString();
        String unit = binding.spinnerUnit.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dosage)) {
            Medication newMedication = new Medication(name, dosage, unit, false, false);
            viewModel.addMedication(newMedication);
            Toast.makeText(getApplicationContext(), "Medication Created", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
        }
    }

    private void editMedication() {
        String name = binding.editTextMedicationName.getText().toString();
        String dosage = binding.editTextDosage.getText().toString();
        String unit = binding.spinnerUnit.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dosage)) {
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setUnit(unit);
            viewModel.updateMedication(medication);
            Toast.makeText(getApplicationContext(), "Medication Updated", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
        }
    }
}
