package com.example.insight.view;

import static com.example.insight.utility.AlarmStaticUtils.generateUniqueRequestCode;
import static com.example.insight.utility.AlarmStaticUtils.getDayIndex;
import static com.example.insight.utility.AlarmStaticUtils.getHour;
import static com.example.insight.utility.AlarmStaticUtils.getMinute;
import static com.example.insight.utility.AlarmStaticUtils.getNextAlarmTime;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
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

        allocateActivityTitle("Edit Alarm");

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

        //make sure notifications permission is granted for app
        //android 33 and lower set default is set to enabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Always try launching it
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

                // THEN check if it's blocked permanently
                if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Notification Permission Required")
                            .setMessage("To receive medication reminders, you need to enable notification permission.\n\nPlease enable it manually in app settings to receive reminders.")
                            .setPositiveButton("Open Settings", (dialog, which) -> {
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // For android 8 (api 26) and above
                                    // by default it goes to app main page
                                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                } else {
                                    // for older android it opens the notification page for the app
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                }
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }

                return;
            }
        }

        // Make sure channel is enabled too
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = notificationManager.getNotificationChannel("alarm_channel");

        if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            new AlertDialog.Builder(this)
                    .setTitle("Medication Reminders Disabled")
                    .setMessage("The Medication Reminders channel is disabled. Please enable it in settings to receive alarm notifications.")
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, "alarm_channel");
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return; // Stop saving if channel is off
        }

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
        setResult(RESULT_OK); // This signals that changes were made
        finish();
    }

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications enabled! You can now set alarms.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied. Enable it in settings to receive reminders.", Toast.LENGTH_LONG).show();
                }
            });


}
