package com.example.insight.view;

import static com.example.insight.utility.AlarmStaticUtils.generateUniqueRequestCode;
import static com.example.insight.utility.AlarmStaticUtils.getNextAlarmTime;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;


import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
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
    private String selectedTime = null; // Default time


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //allocateActivityTitle("Add Alarm");

        // Retrieve medicationID (and optionally medicationName, dosage) from Intent
        medicationId = getIntent().getStringExtra("medicationID");
        String medicationName = getIntent().getStringExtra("medicationName");
        String dosage = getIntent().getStringExtra("dosage");


        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);


        // ✅ Call this method here
        checkMedicationReminderChannel();

        // Set up the time picker button
//        binding.btnPickTime.setOnClickListener(v -> {
//            MaterialTimePicker picker = new MaterialTimePicker.Builder()
//                    .setTimeFormat(TimeFormat.CLOCK_12H)
//                    .setHour(8)
//                    .setMinute(0)
//                    .setTitleText("Select Alarm Time")
//                    .build();
//            picker.show(getSupportFragmentManager(), "TIME_PICKER");
//            picker.addOnPositiveButtonClickListener(view -> {
//                int hour = picker.getHour();
//                int minute = picker.getMinute();
//                String amPm = (hour >= 12) ? "PM" : "AM";
//                int displayHour = hour % 12;
//                if (displayHour == 0) displayHour = 12;
//                selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm);
//                binding.textSelectedTime.setText("Selected time: " + selectedTime);
//            });
//        });

        binding.textSelectedTime.setText("No time selected");


        binding.btnPickTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(8)
                    .setMinute(0)
                    .setTitleText("Select Alarm Time")
                    .build();

            picker.show(getSupportFragmentManager(), "TIME_PICKER");

            picker.addOnPositiveButtonClickListener(view -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                selectedTime = String.format("%02d:%02d", hour, minute);
                binding.textSelectedTime.setText("Selected time: " + selectedTime);
            });
        });


        // Set up the Save Alarm button
        binding.btnSaveAlarm.setOnClickListener(v -> {

            if (hasValidationError()) return;

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


            List<MedicationAlarm> alarmsToProcess = new ArrayList<>();

            // Collect alarms for selected days
            if (binding.checkMonday.isChecked()) alarmsToProcess.add(createAlarm("Monday"));
            if (binding.checkTuesday.isChecked()) alarmsToProcess.add(createAlarm("Tuesday"));
            if (binding.checkWednesday.isChecked()) alarmsToProcess.add(createAlarm("Wednesday"));
            if (binding.checkThursday.isChecked()) alarmsToProcess.add(createAlarm("Thursday"));
            if (binding.checkFriday.isChecked()) alarmsToProcess.add(createAlarm("Friday"));
            if (binding.checkSaturday.isChecked()) alarmsToProcess.add(createAlarm("Saturday"));
            if (binding.checkSunday.isChecked()) alarmsToProcess.add(createAlarm("Sunday"));


            // Get the current locally stored alarms
            List<MedicationAlarm> localAlarms = AlarmLocalStorageHelper.getAlarms(AddAlarmActivity.this);

            // For each alarm, add/update as needed
            for (MedicationAlarm alarm : alarmsToProcess) {

                alarm.setMedicationName(medicationName);
                alarm.setDosage(dosage);
                Log.d("AddAlarmActivity", "Saving alarm: " +
                        "Day=" + alarm.getDay() +
                        ", Time=" + alarm.getTime() +
                        ", MedID=" + alarm.getMedicationId() +
                        ", AlarmID=" + alarm.getAlarmId());
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
            setResult(RESULT_OK); // This signals that changes were made
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

    private boolean hasValidationError(){
        boolean hasError = false;

// Reset visibility
        binding.textErrorDays.setVisibility(View.INVISIBLE);
        binding.textErrorTime.setVisibility(View.GONE);

// Validate time
        if (selectedTime == null) {
            binding.textSelectedTime.setVisibility(View.GONE);
            binding.textErrorTime.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else{
            binding.textSelectedTime.setVisibility(View.VISIBLE);
        }


// Validate day selection
        boolean anyDayChecked =
                binding.checkMonday.isChecked() || binding.checkTuesday.isChecked() ||
                        binding.checkWednesday.isChecked() || binding.checkThursday.isChecked() ||
                        binding.checkFriday.isChecked() || binding.checkSaturday.isChecked() ||
                        binding.checkSunday.isChecked();

        if (!anyDayChecked) {
            binding.textErrorDays.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else{
            binding.textErrorDays.setVisibility(View.INVISIBLE);
        }

        return hasError;
    }

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications enabled! You can now set alarms.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied. Enable it in settings to receive reminders.", Toast.LENGTH_LONG).show();
                }
            });

    private void checkMedicationReminderChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = notificationManager.getNotificationChannel("alarm_channel");
            if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                new AlertDialog.Builder(this)
                        .setTitle("Medication Reminders Disabled")
                        .setMessage("The Medication Reminders notification channel is turned off. You won't receive alarms unless it's enabled.\n\nWould you like to open settings to enable it?")
                        .setPositiveButton("Open Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, "alarm_channel");
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            Toast.makeText(this, "Reminders won't work unless the channel is enabled.", Toast.LENGTH_LONG).show();
                        })
                        .show();
            }
        }
    }

}
