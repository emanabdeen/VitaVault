package com.example.insight.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityVitalDetailsBinding;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.TimeValidator;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

public class VitalDetails extends DrawerBaseActivity {
    ActivityVitalDetailsBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    VitalViewModel viewModel;
    String pageFunction;
    String vitalId;
    String vitalType;
    String unit;
    Vital vital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVitalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Vitals");

        //inst ViewModel that handles fetching the vitals details
        viewModel=new ViewModelProvider(this).get(VitalViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        //get the vital type from the intent
        Intent intentObject = getIntent();

        //set the title of the page according to the vital type
        binding.textViewTitle.setText(intentObject.getStringExtra("title"));

        //set the image of the page according to the vital type
        String imageName = intentObject.getStringExtra("image");
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);



        //if vitalId has value, so this page is edit vital
        if (!intentObject.getStringExtra("vitalID").isEmpty()){

            pageFunction="editVital";
            vitalId=intentObject.getStringExtra("vitalID");
            viewModel.GetVitalById(vitalId);

            //to observe the data and display it
            viewModel.getSelectedVitalData().observe(this,selectedVitalData ->{
                vital= selectedVitalData; //vital object

                //set the input fields values only when object is retrieved
                if (vital.getRecordDate()!=null && vital.getRecordTime()!=null){
                    vitalType = vital.getVitalType();
                    unit = vital.getUnit();

                    //add the retrieved vital data into the input fields
                    binding.editTextDate.setText(DateValidator.LocalDateToString(selectedVitalData.getRecordDate()));
                    binding.editTime.setText(TimeValidator.LocalTimeToString(selectedVitalData.getRecordTime()));
                    binding.editTextMeasure1.setText(selectedVitalData.getMeasurement1());

                    //if the vital is not blood pressure, hide measure2 and put the measurement label = unit
                    if (!Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                        binding.editTextMeasure2.setVisibility(View.GONE);
                        binding.lblMeasure2.setVisibility(View.GONE);
                        binding.lblMeasure1.setText(unit);
                    }
                    //if the vital is blood pressure, hide measure2 and put the measurement labels Systolic ,Diastolic
                    if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                        binding.lblMeasure1.setText("Systolic " + unit);
                        binding.lblMeasure2.setText("Diastolic " + unit);
                        binding.editTextMeasure2.setText(selectedVitalData.getMeasurement2());
                    }
                }

            });
        }else{
            //if vitalId does not have value, so this page is create new vital
            pageFunction = "createVital";
            vitalType = intentObject.getStringExtra("vitalType");
            unit = intentObject.getStringExtra("unit");

            //put initial values for current time and date
            binding.editTextDate.setText(DateValidator.LocalDateToString(LocalDate.now()));
            binding.editTime.setText(TimeValidator.LocalTimeToString(LocalTime.now()));

            //if the vital is not blood pressure, hide measure2 and put the measurement label = unit
            if (!Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                binding.editTextMeasure2.setVisibility(View.GONE);
                binding.lblMeasure2.setVisibility(View.GONE);
                binding.lblMeasure1.setText(unit);
            }
            //if the vital is blood pressure, hide measure2 and put the measurement labels Systolic ,Diastolic
            if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                binding.lblMeasure1.setText("Systolic " + unit);
                binding.lblMeasure2.setText("Diastolic " + unit);
            }
        }

        // Set up date pickers for start date inputs
        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.editTextDate);
            }
        });

        // Set up time pickers for start and end date inputs
        binding.editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(binding.editTime);
            }
        });

        //  Save Button
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (pageFunction.equals("createVital")){
                        CreateNewVital();
                    }
                    if (pageFunction.equals("editVital")){
                        EditVital();
                    }

                }catch (Exception e) {
                    Log.e("error", "try-catch error: "+ e.getMessage());
                }

            }
        });
    }

    private void CreateNewVital() {

        try{
            VitalViewModel vitalViewModel = new VitalViewModel();
            String uid = user.getUid(); // Get the logged-in user's unique ID

            // Get data from UI
            String recordDateStr = binding.editTextDate.getText().toString();
            String recordTimeStr = binding.editTime.getText().toString();
            String recordMeasure1 = binding.editTextMeasure1.getText().toString();
            String recordMeasure2 = binding.editTextMeasure2.getText().toString();

            if (!TextUtils.isEmpty(recordDateStr) && !TextUtils.isEmpty(recordTimeStr) && !TextUtils.isEmpty(recordMeasure1)) {
                boolean isDateValid = DateValidator.isValidDate(recordDateStr);
                boolean isTimeValid = TimeValidator.isValidTime(recordTimeStr);
                showError(binding.errorGeneral, "", false);

                // Handle date error
                showError(binding.errorDate, "Invalid Date (e.g., 2025-05-15)", !isDateValid);

                // Handle time error
                showError(binding.errorTime, "Invalid Time (e.g., 14:30)", !isTimeValid);

                if (isDateValid && isTimeValid) {
                    LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);
                    LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);

                    if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                        if (!TextUtils.isEmpty(recordMeasure2)) {

                            // Initialize and save Vital object
                            Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
                            newVital.setMeasurement1(recordMeasure1);
                            newVital.setMeasurement2(recordMeasure2);

                            //add the record to firestore
                            vitalViewModel.AddVital(newVital);
                            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            // Handle missing field error
                            showError(binding.errorGeneral, "One or more fields are empty.", true);
                            Log.e("error", "One or more fields are empty.");
                        }
                    } else if (VitalsCategories.isValidVitalCategory(vitalType)) {
                        showError(binding.errorGeneral, "", false);

                        // Initialize and save Vital object
                        Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
                        newVital.setMeasurement1(recordMeasure1);

                        //add the record to firestore
                        vitalViewModel.AddVital(newVital);
                        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.d("Activity", "Could not define the vital type");
                    }
                }
            } else {
                //Handle missing field error
                showError(binding.errorGeneral, "One or more fields are empty.", true);
                Log.e("error", "One or more fields are empty.");
            }

        }catch (Exception e) {
            Log.e("error", "try-catch error: "+ e.getMessage());
        }
    }
    private void EditVital(){
        try{
            VitalViewModel vitalViewModel = new VitalViewModel();

            // Get data from UI
            String recordDateStr = binding.editTextDate.getText().toString();
            String recordTimeStr = binding.editTime.getText().toString();
            String recordMeasure1 = binding.editTextMeasure1.getText().toString();
            String recordMeasure2 = StringHandler.defaultIfNull(binding.editTextMeasure2.getText());

            if (!TextUtils.isEmpty(recordDateStr) && !TextUtils.isEmpty(recordTimeStr) && !TextUtils.isEmpty(recordMeasure1)) {
                boolean isDateValid = DateValidator.isValidDate(recordDateStr);
                boolean isTimeValid = TimeValidator.isValidTime(recordTimeStr);
                showError(binding.errorGeneral, "", false);

                // Handle date error
                showError(binding.errorDate, "Invalid Date (e.g., 2025-05-15)", !isDateValid);

                // Handle time error
                showError(binding.errorTime, "Invalid Time (e.g., 14:30)", !isTimeValid);

                if (isDateValid && isTimeValid) {
                    LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);
                    LocalTime recordTime = TimeValidator.StringToLocalTime(recordTimeStr);

                    if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                        if (!TextUtils.isEmpty(recordMeasure2)) {

                            // Initialize and save Vital object
                            Vital updatedVital = new Vital(recordDate, recordTime, vitalType, unit);
                            updatedVital.setMeasurement1(recordMeasure1);
                            updatedVital.setMeasurement2(recordMeasure2);
                            updatedVital.setVitalId(vitalId);


                            //add the record to firestore
                            vitalViewModel.UpdateVital(updatedVital);
                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            // Handle missing field error
                            showError(binding.errorGeneral, "One or more fields are empty.", true);
                            Log.e("error", "One or more fields are empty.");
                        }
                    } else if (VitalsCategories.isValidVitalCategory(vitalType)) {
                        showError(binding.errorGeneral, "", false);

                        // Initialize and save Vital object
                        Vital updatedVital = new Vital(recordDate, recordTime, vitalType, unit);
                        updatedVital.setMeasurement1(recordMeasure1);
                        updatedVital.setVitalId(vitalId);

                        //add the record to firestore
                        vitalViewModel.UpdateVital(updatedVital);
                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.d("Activity", "Could not define the vital type");
                    }
                }
            } else {
                //Handle missing field error
                showError(binding.errorGeneral, "One or more fields are empty.", true);
                Log.e("error", "One or more fields are empty.");
            }

        }catch (Exception e) {
            Log.e("error", "try-catch error: "+ e.getMessage());
        }
    }
    private void showDatePicker(TextInputEditText dateInput) {
        // Get current date
        Calendar calendar = Calendar.getInstance();

        // Create constraints for date selection (optional)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Create MaterialDatePicker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(calendar.getTimeInMillis()) // Set current date as default
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert milliseconds to Calendar
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selection);

            // Format the selected date (YYYY-MM-DD)
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH) + 1; // +1 because months are 0-based
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH)+1;

            String selectedDate = String.format("%04d-%02d-%02d", year, month, day);
            dateInput.setText(selectedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
    private void showTimePicker(TextInputEditText timeInput) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create MaterialTimePicker with 24-hour format
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // Use 24-hour format
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK) // Forces clock view
                .build();

        picker.addOnPositiveButtonClickListener(view -> {
            // Format the selected time in 24-hour format and set it to the input field
            String selectedTime = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
            timeInput.setText(selectedTime);
        });

        picker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    /*private void showDatePicker(TextInputEditText dateInput) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it to the input field
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }*/

    /*private void showTimePicker(TextInputEditText timeInput) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Format the selected time and set it to the input field
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(selectedTime);
                },
                hour, minute, true // true for 24-hour format
        );
        timePickerDialog.show();
    }*/

}