package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insight.databinding.ActivityAddLogBinding;
import com.example.insight.utility.DatePickerValidator;
import com.example.insight.utility.MedicationLogUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditLogActivity extends DrawerBaseActivity {

    private ActivityAddLogBinding binding;
    String medicationId;
    String dosage;
    private String logId;
    private String status;
    private String date;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        medicationId = intent.getStringExtra("medicationID");
        dosage = intent.getStringExtra("dosage");
        logId = intent.getStringExtra("logID");
        status = intent.getStringExtra("status");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

// Fill the fields
        binding.editTextDate.setText(date);
        binding.editTime.setText(time);
        binding.radioDismissed.setVisibility(View.VISIBLE);



        if ("Taken".equalsIgnoreCase(status)) {
            binding.radioTaken.setChecked(true);
        } else if ("Missed".equalsIgnoreCase(status)) {
            binding.radioMissed.setChecked(true);
        }else{
            binding.radioDismissed.setChecked(true);
        }


        // Set up pickers
        binding.editTextDate.setOnClickListener(v -> showMaterialDatePicker());
        binding.editTime.setOnClickListener(v -> showMaterialTimePicker());

        // Save button click
        binding.btnSave.setOnClickListener(v -> saveLog());
    }

    private void showMaterialDatePicker() {
        Calendar calendar = Calendar.getInstance(); // Local time zone
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayMillis = calendar.getTimeInMillis();

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(todayMillis)
                .setValidator(new DatePickerValidator())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(todayMillis)
                .setCalendarConstraints(constraints)
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selection (UTC millis) into UTC calendar to extract the correct date
            Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utcCalendar.setTimeInMillis(selection);

            int year = utcCalendar.get(Calendar.YEAR);
            int month = utcCalendar.get(Calendar.MONTH) + 1; // MONTH is 0-based
            int day = utcCalendar.get(Calendar.DAY_OF_MONTH);

            // Format as yyyy-MM-dd (what your app expects)
            String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            binding.editTextDate.setText(formattedDate);
        });

    }




    private void showMaterialTimePicker() {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(currentHour)
                .setMinute(currentMinute)
                .setTitleText("Select Time")
                .build();

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");

        timePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();

            String selectedDateStr = binding.editTextDate.getText().toString().trim();
            LocalDate selectedDate;

            try {
                selectedDate = LocalDate.parse(selectedDateStr);
            } catch (Exception e) {
                // If somehow the date is invalid or blank
                Toast.makeText(this, "Invalid or missing date. Defaulting to today.", Toast.LENGTH_SHORT).show();
                selectedDate = LocalDate.now(); // Fallback to today
            }

            LocalDate today = LocalDate.now();

            if (selectedDate.equals(today)) {
                if (selectedHour > currentHour || (selectedHour == currentHour && selectedMinute > currentMinute)) {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    binding.editTime.setText(formattedTime);
                    showError(binding.errorTime, "Selected time cannot be in the future.", true);
                    return;
                }
            }
            else{
                showError(binding.errorTime, "", false);
            }

            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            binding.editTime.setText(formattedTime);
        });
    }



    private void saveLog() {
        String status = null;

        if (binding.radioTaken.isChecked()) {
            status = binding.radioTaken.getText().toString();
        } else if (binding.radioMissed.isChecked()) {
            status = binding.radioMissed.getText().toString();
        }
        else if (binding.radioDismissed.getVisibility() == View.VISIBLE && binding.radioDismissed.isChecked()) {
            status = binding.radioDismissed.getText().toString();
        }

        String date = binding.editTextDate.getText().toString().trim();
        String time = binding.editTime.getText().toString().trim();

        boolean isValid = true;

        showError(binding.errorDate, "", false);
        showError(binding.errorTime, "", false);
        showError(binding.errorStatus, "", false);
        showError(binding.errorGeneral, "", false);

        if (TextUtils.isEmpty(date)) {
            showError(binding.errorDate, "Please select a valid date", true);
            isValid = false;
        }

        if (TextUtils.isEmpty(time)) {
            showError(binding.errorTime, "Please select a valid time", true);
            isValid = false;
        }

        if (status == null) {
            showError(binding.errorStatus, "Please select a status", true);
            isValid = false;
        }

        try {
            String dateStr = binding.editTextDate.getText().toString().trim();
            String timeStr = binding.editTime.getText().toString().trim();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date selectedDateTime = format.parse(dateStr + " " + timeStr);

            Date now = new Date();

            if (selectedDateTime != null && selectedDateTime.after(now)) {
                showError(binding.errorTime, "Selected time cannot be in the future.", true);
                isValid = false;
            }
        } catch (Exception e) {
            showError(binding.errorGeneral, "Invalid date or time format", true);
            isValid = false;
        }

        if (!isValid) return;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date timestamp = format.parse(date + " " + time);

            MedicationLogUtils.updateMedicationLog(
                    this,
                    medicationId,
                    logId,  // <- Make sure this is passed from intent!
                    status,
                    timestamp,
                    new MedicationLogUtils.MedicationLogCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EditLogActivity.this, "✅ Log updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showError(binding.errorGeneral, "❌ Failed to update log", true);
                            Log.e("EditLogActivity", "❌ Update failed", e);
                        }
                    }
            );

        } catch (Exception e) {
            showError(binding.errorGeneral, "Invalid date or time format", true);
            e.printStackTrace();
        }
    }


    private void showError(TextView view, String message, boolean show) {
        if (show) {
            view.setText(message);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
