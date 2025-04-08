package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

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
            allocateActivityTitle("Medication Details");
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


        } else {
            allocateActivityTitle("Add Medication");
            pageFunction = "createMedication";
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

        // Clear errors on focus
        binding.editTextMedicationName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.textErrorMedicationName.setVisibility(View.INVISIBLE);
        });

        binding.editTextDosage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.textErrorDosage.setVisibility(View.INVISIBLE);
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

    /**
     * Validate fields and show red error messages if empty
     */
    private boolean validateMedicationFields() {
        boolean isValid = true;

        String name = binding.editTextMedicationName.getText().toString().trim();
        String dosage = binding.editTextDosage.getText().toString().trim();

        // Reset errors
        binding.textErrorMedicationName.setVisibility(View.GONE);
        binding.textErrorDosage.setVisibility(View.GONE);

        if (TextUtils.isEmpty(name)) {
            binding.textErrorMedicationName.setText("Medication Name is required");
            binding.textErrorMedicationName.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (TextUtils.isEmpty(dosage)) {
            binding.textErrorDosage.setText("Dosage is required");
            binding.textErrorDosage.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Save new medication after validation
     */
    private void createNewMedication() {
        if (!validateMedicationFields()) return;

        String name = binding.editTextMedicationName.getText().toString().trim();
        String dosage = binding.editTextDosage.getText().toString().trim();
        String unit = binding.spinnerUnit.getSelectedItem().toString();


        Medication newMedication = new Medication(name, dosage, unit);
        viewModel.addMedication(newMedication);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Edit existing medication after validation
     */
    private void editMedication() {
        if (!validateMedicationFields()) return;

        String name = binding.editTextMedicationName.getText().toString().trim();
        String dosage = binding.editTextDosage.getText().toString().trim();
        String unit = binding.spinnerUnit.getSelectedItem().toString();

        medication.setName(name);
        medication.setDosage(dosage);
        medication.setUnit(unit);
        viewModel.updateMedication(medication);
        setResult(RESULT_OK);
        finish();
    }
}
