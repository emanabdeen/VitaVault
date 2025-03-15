package com.example.insight.view;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityAddAlarmBinding;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.utility.AlarmHelper;
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
    private boolean isEdit = false;
    private String existingAlarmId = null; // Only used in edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve medicationID (and optionally medicationName, dosage) from Intent
        medicationId = getIntent().getStringExtra("medicationID");
        String medicationName = getIntent().getStringExtra("medicationName");
        String dosage = getIntent().getStringExtra("dosage");

        // Check if we're editing an existing alarm.
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            existingAlarmId = getIntent().getStringExtra("alarmId");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);

        // Set default state: alarm enabled by default
        binding.switchReminder.setChecked(true);
        binding.alarmFields.setVisibility(View.VISIBLE);

        // Toggle alarm fields visibility when the switch is changed
        binding.switchReminder.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.alarmFields.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

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

        // If editing, preload the alarm details
        if (isEdit) {
            String day = getIntent().getStringExtra("day");
            String time = getIntent().getStringExtra("time");
            String repeatInfo = getIntent().getStringExtra("repeatInfo");

            selectedTime = time;
            binding.textSelectedTime.setText("Selected time: " + time);
            binding.switchRepeating.setChecked("Repeats weekly".equalsIgnoreCase(repeatInfo));

            // Pre-check the corresponding day checkbox (assumes helper method below)
            CheckBox dayCheckbox = getCheckBoxForDay(day);
            if (dayCheckbox != null) {
                dayCheckbox.setChecked(true);
            }
        }

        // Set up the Save Alarm button
        binding.btnSaveAlarm.setOnClickListener(v -> {
            List<MedicationAlarm> alarmsToProcess = new ArrayList<>();

            // In this example, we assume one alarm per day.
            // In edit mode, you might update only the alarm for the selected day.
            if (binding.checkMonday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Monday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkTuesday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Tuesday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkWednesday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Wednesday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkThursday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Thursday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkFriday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Friday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkSaturday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Saturday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }
            if (binding.checkSunday.isChecked()) {
                MedicationAlarm alarm = new MedicationAlarm();
                alarm.setDay("Sunday");
                alarm.setTime(selectedTime);
                alarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                alarm.setEnabled(true);
                alarmsToProcess.add(alarm);
            }

            if (alarmsToProcess.isEmpty()) {
                Toast.makeText(AddAlarmActivity.this, "Please select at least one day for the alarm.", Toast.LENGTH_SHORT).show();
                return;
            }

            // For each alarm (in edit mode, typically there will be one alarm to update)
            for (MedicationAlarm alarm : alarmsToProcess) {
                // In edit mode, cancel the previously scheduled alarm before updating.
                if (isEdit) {
                    // Retrieve old alarm details from intent extras.
                    String oldDay = getIntent().getStringExtra("day");
                    String oldTime = getIntent().getStringExtra("time");

                    // Compare the old values with the updated ones (for simplicity, here we assume the day doesn't change)
                    boolean timeChanged = !oldTime.equals(selectedTime);

                    if (timeChanged) {
                        // Generate the old request code using the old values.
                        int oldRequestCode = generateUniqueRequestCode(medicationId, oldDay, oldTime);
                        // Cancel the old alarm.
                        AlarmHelper.cancelAlarm(AddAlarmActivity.this, oldRequestCode, medicationId, medicationName);
                    }

                    // Create a new alarm object using the updated values.
                    MedicationAlarm updatedAlarm = new MedicationAlarm();
                    updatedAlarm.setDay(oldDay);  // assuming day remains the same in edit mode
                    updatedAlarm.setTime(selectedTime); // new time if the user changed it
                    updatedAlarm.setRepeatInfo(binding.switchRepeating.isChecked() ? "Repeats weekly" : "One-time");
                    updatedAlarm.setEnabled(true);
                    // IMPORTANT: Set the alarmId to the existing one so the correct document is updated.
                    updatedAlarm.setAlarmId(existingAlarmId);
                    updatedAlarm.setMedicationId(medicationId);

                    // Update Firestore with the new alarm details.
                    viewModel.updateAlarm(medicationId, updatedAlarm);

                    // Schedule the new alarm with the updated values.
                    Calendar alarmCalendar = getNextAlarmTime(updatedAlarm.getDay(), updatedAlarm.getTime());
                    int newRequestCode = generateUniqueRequestCode(medicationId, updatedAlarm.getDay(), updatedAlarm.getTime());
                    AlarmHelper.setAlarm(AddAlarmActivity.this, newRequestCode, alarmCalendar, medicationId, medicationName, binding.switchRepeating.isChecked(), dosage);
                } else {
                    // For new alarms, add the alarm document.
                    viewModel.addAlarm(medicationId, alarm);
                }

                // Now, schedule the new alarm on the phone.
                Calendar alarmCalendar = getNextAlarmTime(alarm.getDay(), alarm.getTime());
                int newRequestCode = generateUniqueRequestCode(medicationId, alarm.getDay(), alarm.getTime());
                AlarmHelper.setAlarm(
                        AddAlarmActivity.this,
                        newRequestCode,
                        alarmCalendar,
                        medicationId,
                        medicationName,
                        binding.switchRepeating.isChecked(),
                        dosage
                );
            }
            Toast.makeText(AddAlarmActivity.this, "Alarm(s) saved successfully.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Helper to calculate the next alarm time based on day and time string (e.g., "08:00 AM")
    private Calendar getNextAlarmTime(String day, String time) {
        Calendar calendar = Calendar.getInstance();
        String[] timeParts = time.split(" ");
        String[] hourMin = timeParts[0].split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int minute = Integer.parseInt(hourMin[1]);
        String amPm = timeParts[1];
        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day));
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return calendar;
    }

    // Helper method: converts day string to Calendar constant.
    private int getDayOfWeek(String day) {
        switch (day) {
            case "Sunday": return Calendar.SUNDAY;
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            default: return Calendar.MONDAY;
        }
    }

    // Generate a unique request code based on medicationId, day, and time.
    private int generateUniqueRequestCode(String medicationId, String day, String time) {
        return (medicationId + day + time).hashCode();
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
}
