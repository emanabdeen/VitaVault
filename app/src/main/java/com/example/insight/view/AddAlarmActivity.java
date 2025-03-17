package com.example.insight.view;

import static com.example.insight.utility.AlarmStaticUtils.generateUniqueRequestCode;
import static com.example.insight.utility.AlarmStaticUtils.getNextAlarmTime;

import android.os.Bundle;

import android.widget.CheckBox;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityAddAlarmBinding;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.utility.AlarmLocalStorageHelper;
import com.example.insight.viewmodel.MedicationAlarmsViewModel;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddAlarmActivity extends DrawerBaseActivity {

    private ActivityAddAlarmBinding binding;
    private MedicationAlarmsViewModel viewModel;
    private String medicationId;
    private String selectedTime = "08:00 AM"; // Default time


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve medicationID (and optionally medicationName, dosage) from Intent
        medicationId = getIntent().getStringExtra("medicationID");
        String medicationName = getIntent().getStringExtra("medicationName");
        String dosage = getIntent().getStringExtra("dosage");


        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);


        // Set up the time picker button
        binding.btnPickTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(8)
                    .setMinute(0)
                    .setTitleText("Select Alarm Time")
                    .build();
            picker.show(getSupportFragmentManager(), "TIME_PICKER");
            picker.addOnPositiveButtonClickListener(view -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                String amPm = (hour >= 12) ? "PM" : "AM";
                int displayHour = hour % 12;
                if (displayHour == 0) displayHour = 12;
                selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm);
                binding.textSelectedTime.setText("Selected time: " + selectedTime);
            });
        });


        // Set up the Save Alarm button
        binding.btnSaveAlarm.setOnClickListener(v -> {
            List<MedicationAlarm> alarmsToProcess = new ArrayList<>();

            // Collect alarms for selected days
            if (binding.checkMonday.isChecked()) alarmsToProcess.add(createAlarm("Monday"));
            if (binding.checkTuesday.isChecked()) alarmsToProcess.add(createAlarm("Tuesday"));
            if (binding.checkWednesday.isChecked()) alarmsToProcess.add(createAlarm("Wednesday"));
            if (binding.checkThursday.isChecked()) alarmsToProcess.add(createAlarm("Thursday"));
            if (binding.checkFriday.isChecked()) alarmsToProcess.add(createAlarm("Friday"));
            if (binding.checkSaturday.isChecked()) alarmsToProcess.add(createAlarm("Saturday"));
            if (binding.checkSunday.isChecked()) alarmsToProcess.add(createAlarm("Sunday"));

            if (alarmsToProcess.isEmpty()) {
                Toast.makeText(AddAlarmActivity.this, "Please select at least one day for the alarm.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current locally stored alarms
            List<MedicationAlarm> localAlarms = AlarmLocalStorageHelper.getAlarms(AddAlarmActivity.this);

            // For each alarm, add/update as needed
            for (MedicationAlarm alarm : alarmsToProcess) {

                alarm.setMedicationName(medicationName);
                alarm.setDosage(dosage);
                viewModel.addAlarm(medicationId, alarm);
                localAlarms.add(alarm);


                // Schedule the alarm on the phone.
                Calendar alarmCalendar = getNextAlarmTime(alarm.getDay(), alarm.getTime());
                int newRequestCode = generateUniqueRequestCode(medicationId, alarm.getDay(), alarm.getTime());
                AlarmHelper.setAlarm(
                        AddAlarmActivity.this,
                        newRequestCode,
                        alarmCalendar,
                        medicationId,
                        medicationName,
                        true, // Always recurring by default
                        dosage
                );
            }

            // Save the updated list of alarms locally
            AlarmLocalStorageHelper.saveAlarms(AddAlarmActivity.this, localAlarms);

            Toast.makeText(AddAlarmActivity.this, "Alarm(s) saved successfully.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Helper method to retrieve the CheckBox for a given day.
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

    private MedicationAlarm createAlarm(String day) {
        MedicationAlarm alarm = new MedicationAlarm();
        alarm.setDay(day);
        alarm.setTime(selectedTime);
        alarm.setMedicationId(medicationId);
        alarm.setMedicationName(getIntent().getStringExtra("medicationName"));
        alarm.setDosage(getIntent().getStringExtra("dosage"));
        return alarm;
    }
}
