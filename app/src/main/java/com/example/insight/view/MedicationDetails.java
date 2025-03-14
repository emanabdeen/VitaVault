package com.example.insight.view;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityMedicationDetailsBinding;
import com.example.insight.model.Medication;
import com.example.insight.viewmodel.MedicationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MedicationDetails extends DrawerBaseActivity {
    private ActivityMedicationDetailsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MedicationViewModel viewModel;
    private String pageFunction;
    private String medicationId;
    private Medication medication;
    private String selectedTime = "08:00 AM"; // Default time

    private final List<String> daysOfWeek = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

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

        // Setup Unit Spinner
        setupUnitSpinner();

        // Toggle Reminder Fields Visibility
        binding.switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.reminderFields.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Time Picker Button
        binding.btnPickTime.setOnClickListener(v -> showTimePickerDialog());

        // Handle Intent Data (Editing or Creating New)
        Intent intent = getIntent();
        binding.textViewTitle.setText(intent.getStringExtra("title"));

        if (!TextUtils.isEmpty(intent.getStringExtra("medicationID"))) {
            pageFunction = "editMedication";
            medicationId = intent.getStringExtra("medicationID");

            viewModel.getMedications();
            viewModel.getMedicationsData().observe(this, medications -> {
                if (medications == null) {
                    Log.d("MedicationDetails", "Waiting for Firestore data...");
                    return;
                }

                for (Medication m : medications) {
                    if (m.getMedicationId().equals(medicationId)) {
                        medication = m;
                        populateFields(m);
                        break;
                    }
                }
            });
        } else {
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
                Log.e("error", "try-catch error: " + e.getMessage());
            }
        });
    }

    private void setupUnitSpinner() {
        List<String> units = Arrays.asList("mg", "mL", "tablets", "puffs", "drops");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        binding.spinnerUnit.setAdapter(adapter);
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String amPm = (hourOfDay >= 12) ? "PM" : "AM";
            int hour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
            if (hour == 0) hour = 12;

            selectedTime = String.format("%02d:%02d %s", hour, minute, amPm);
            binding.textSelectedTime.setText("Selected time: " + selectedTime);
        }, 8, 0, false); // Default time: 08:00 AM

        timePicker.show();
    }

    private void populateFields(Medication medication) {
        binding.editTextMedicationName.setText(medication.getName());
        binding.editTextDosage.setText(medication.getDosage());
        binding.switchReminder.setChecked(medication.isReminderEnabled());

        // Set correct unit in spinner
        String unit = medication.getUnit();
        if (unit != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinnerUnit.getAdapter();
            int position = adapter.getPosition(unit);
            binding.spinnerUnit.setSelection(position);
        }

        // Set Reminder Days & Time
        if (medication.isReminderEnabled()) {
            binding.reminderFields.setVisibility(View.VISIBLE);
            HashMap<String, List<String>> reminderMap = medication.getReminderMap();

            for (String day : daysOfWeek) {
                if (reminderMap.containsKey(day)) {
                    CheckBox checkBox = getCheckBoxForDay(day);
                    if (checkBox != null) checkBox.setChecked(true);
                    selectedTime = reminderMap.get(day).get(0);
                    binding.textSelectedTime.setText("Selected time: " + selectedTime);
                }
            }
        }
    }

    private void createNewMedication() {
        try {
            String name = binding.editTextMedicationName.getText().toString();
            String dosage = binding.editTextDosage.getText().toString();
            String unit = binding.spinnerUnit.getSelectedItem().toString();
            boolean reminderEnabled = binding.switchReminder.isChecked();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dosage)) {
                Medication newMedication = new Medication(name, dosage, unit, reminderEnabled);
                if (reminderEnabled) {
                    newMedication.setReminderMap(collectReminderTimes());
                }

                viewModel.addMedication(newMedication);
                Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("error", "try-catch error: " + e.getMessage());
        }
    }

    private void editMedication() {
        try {
            String name = binding.editTextMedicationName.getText().toString();
            String dosage = binding.editTextDosage.getText().toString();
            String unit = binding.spinnerUnit.getSelectedItem().toString();
            boolean reminderEnabled = binding.switchReminder.isChecked();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dosage)) {
                medication.setName(name);
                medication.setDosage(dosage);
                medication.setUnit(unit);
                medication.setReminderEnabled(reminderEnabled);

                if (reminderEnabled) {
                    medication.setReminderMap(collectReminderTimes());
                } else {
                    medication.setReminderMap(new HashMap<>()); // Clear reminders if disabled
                }

                viewModel.updateMedication(medication);
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("error", "try-catch error: " + e.getMessage());
        }
    }

    private HashMap<String, List<String>> collectReminderTimes() {
        HashMap<String, List<String>> remindersMap = new HashMap<>();

        for (String day : daysOfWeek) {
            CheckBox checkBox = getCheckBoxForDay(day);
            if (checkBox != null && checkBox.isChecked()) {
                remindersMap.put(day, Arrays.asList(selectedTime));
            }
        }
        return remindersMap;
    }

    private CheckBox getCheckBoxForDay(String day) {
        switch (day) {
            case "Monday": return binding.checkMonday;
            case "Tuesday": return binding.checkTuesday;
            case "Wednesday": return binding.checkWednesday;
            case "Thursday": return binding.checkThursday;
            case "Friday": return binding.checkFriday;
            case "Saturday": return binding.checkSaturday;
            case "Sunday": return binding.checkSunday;
            default: return null;
        }
    }
}
