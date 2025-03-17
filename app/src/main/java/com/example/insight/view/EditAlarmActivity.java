package com.example.insight.view;

import static com.example.insight.utility.AlarmStaticUtils.generateUniqueRequestCode;
import static com.example.insight.utility.AlarmStaticUtils.getDayIndex;
import static com.example.insight.utility.AlarmStaticUtils.getHour;
import static com.example.insight.utility.AlarmStaticUtils.getMinute;
import static com.example.insight.utility.AlarmStaticUtils.getNextAlarmTime;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityEditAlarmBinding;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.utility.AlarmLocalStorageHelper;
import com.example.insight.viewmodel.MedicationAlarmsViewModel;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.List;

public class EditAlarmActivity extends DrawerBaseActivity {

    private ActivityEditAlarmBinding binding;
    private MedicationAlarmsViewModel viewModel;
    private String selectedTime;
    private String medicationId, alarmId, medicationName, dosage, originalDay, originalTime;
    private Spinner daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent data
        medicationId = getIntent().getStringExtra("medicationId");
        alarmId = getIntent().getStringExtra("alarmId");
        originalDay = getIntent().getStringExtra("day");
        originalTime = getIntent().getStringExtra("time");
        medicationName = getIntent().getStringExtra("medicationName");
        dosage = getIntent().getStringExtra("dosage");

        selectedTime = originalTime;

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);

        // Setup day spinner
        daySpinner = binding.spinnerDay;
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setSelection(getDayIndex(originalDay));

        // Set initial time
        binding.textSelectedTime.setText("Selected time: " + selectedTime);

        // Set up time picker
        binding.btnPickTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(getHour(originalTime))
                    .setMinute(getMinute(originalTime))
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

        // Set up save button
        binding.btnSaveAlarm.setOnClickListener(v -> saveEditedAlarm());
    }

    private void saveEditedAlarm() {
        String newDay = daySpinner.getSelectedItem().toString();

        // Cancel old alarm if details changed
        if (!newDay.equals(originalDay) || !selectedTime.equals(originalTime)) {
            int oldRequestCode = generateUniqueRequestCode(medicationId, originalDay, originalTime);
            AlarmHelper.cancelAlarm(EditAlarmActivity.this, oldRequestCode, medicationId, medicationName);
        }

        // Create updated alarm object
        MedicationAlarm updatedAlarm = new MedicationAlarm();
        updatedAlarm.setAlarmId(alarmId);
        updatedAlarm.setMedicationId(medicationId);
        updatedAlarm.setDay(newDay);
        updatedAlarm.setTime(selectedTime);
        updatedAlarm.setMedicationName(medicationName);
        updatedAlarm.setDosage(dosage);

        // Update in DB and locally
        viewModel.updateAlarm(medicationId, updatedAlarm);
        List<MedicationAlarm> localAlarms = AlarmLocalStorageHelper.getAlarms(EditAlarmActivity.this);
        AlarmLocalStorageHelper.removeAlarm(EditAlarmActivity.this, new MedicationAlarm(medicationId, originalDay, originalTime, dosage));
        localAlarms.add(updatedAlarm);
        AlarmLocalStorageHelper.saveAlarms(EditAlarmActivity.this, localAlarms);

        // Set the updated alarm
        Calendar alarmCalendar = getNextAlarmTime(newDay, selectedTime);
        int newRequestCode = generateUniqueRequestCode(medicationId, newDay, selectedTime);
        AlarmHelper.setAlarm(EditAlarmActivity.this, newRequestCode, alarmCalendar, medicationId, medicationName, true, dosage);

        Toast.makeText(this, "Alarm updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }



}
