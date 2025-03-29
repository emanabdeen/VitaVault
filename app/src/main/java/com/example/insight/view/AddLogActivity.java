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

public class AddLogActivity extends DrawerBaseActivity {

    private ActivityAddLogBinding binding;
    String medicationId;
    String dosage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        medicationId = intent.getStringExtra("medicationID");
        dosage = intent.getStringExtra("dosage");


        String now = String.format(Locale.getDefault(), "%02d:%02d",
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE));

        binding.editTime.setText(now);
        // Set default date to today
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1; // MONTH is 0-based
        int day = today.get(Calendar.DAY_OF_MONTH);

        String formattedToday = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
        binding.editTextDate.setText(formattedToday);

        // Set up pickers
        binding.editTextDate.setOnClickListener(v -> showMaterialDatePicker());
        binding.editTime.setOnClickListener(v -> showMaterialTimePicker());

        binding.radioTaken.setChecked(true);

        // Save button click
        binding.btnSave.setOnClickListener(v -> saveLog());
    }

    private void showMaterialDatePicker() {
        Calendar calendar = Calendar.getInstance(); // ✅ Local time zone
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
                    Toast.makeText(this, "Cannot select a future time.", Toast.LENGTH_SHORT).show();
                    binding.editTime.setText("");
                    return;
                }
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


        if (!isValid) return;

        Log.d("VitalLog", "Date: " + date + " | Time: " + time + " | Status: " + status);

        try {
            String dateStr = binding.editTextDate.getText().toString().trim();
            String timeStr = binding.editTime.getText().toString().trim();

            if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
                throw new Exception("Empty date or time input");
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date timestamp = format.parse(dateStr + " " + timeStr);


            MedicationLogUtils.logMedicationStatus(
                    this,
                    medicationId,
                    dosage,
                    status,
                    timestamp,
                    false,
                    new MedicationLogUtils.MedicationLogCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AddLogActivity.this, "✅ Log saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showError(binding.errorGeneral, "Failed to save log", true);
                            Log.e("AddLogActivity", "❌ Failed to log", e);
                        }
                    }
            );
        } catch (Exception e) {
            showError(binding.errorGeneral, "Invalid date or time format", true);
        }


        //Toast.makeText(this, "Log saved successfully!", Toast.LENGTH_SHORT).show();
        //finish();
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

