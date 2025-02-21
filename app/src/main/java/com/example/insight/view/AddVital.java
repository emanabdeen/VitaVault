package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.insight.R;
import com.example.insight.databinding.ActivityAddVitalBinding;
import com.example.insight.databinding.ActivityVitalsBinding;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.TimeValidator;
import com.example.insight.utility.Unites;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddVital extends DrawerBaseActivity {
    ActivityAddVitalBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String vitalType;
    String unit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVitalBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        //get the vital type from the intent
        Intent intentObject = getIntent();
        vitalType = intentObject.getStringExtra("vitalType");
        unit = intentObject.getStringExtra("unit");
        binding.textViewTitle.setText(intentObject.getStringExtra("title"));

        String imageName = intentObject.getStringExtra("image");
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);


        binding.editTextDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        binding.editTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        //if the vital not blood pressure, hide measure2
        if (!Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())){
            binding.editTextMeasure2.setVisibility(View.GONE);
            binding.textMeasure2.setVisibility(View.GONE);

        }

        //Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //  Save Button
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewVital();

            }
        });
    }

    private void CreateNewVital() {
//        VitalViewModel vitalViewModel = new VitalViewModel();
//        String uid = user.getUid(); // Get the logged-in user's unique ID
//
//        //get data from UI
//        String recordDateStr = binding.editTextDate.getText().toString();
//        String recordTimeStr = binding.editTime.getText().toString();
//        String recordMeasure1 = binding.editTextMeasure1.getText().toString();
//        String recordMeasure2 = binding.editTextMeasure2.getText().toString();
//
//
//        if (!TextUtils.isEmpty(recordDateStr) && !TextUtils.isEmpty(recordTimeStr) && !TextUtils.isEmpty(recordMeasure1)) {
//
//            boolean isDateValid = DateValidator.isValidDate(recordDateStr);
//            boolean isTimeValid = TimeValidator.isValidTime(recordTimeStr);
//
//            if (!isDateValid) {
//                binding.errorDate.setText("Invalid Date (e.g., 15-05-2025)");
//                binding.errorDate.setVisibility(View.VISIBLE); // Make the TextView visible
//                System.out.println("Date is not valid");
//                Log.d("Activity", "Date is not valid");
//                Log.e("error","Date is not valid");
//
//            }
//            else{binding.errorDate.setVisibility(View.GONE);}
//            if (!isTimeValid) {
//                binding.errorTime.setText("Invalid Date (e.g., 15-05-2025)");
//                binding.errorTime.setVisibility(View.VISIBLE); // Make the TextView visible
//                System.out.println("Time is not valid");
//                Log.d("Activity", "Time is not valid");
//                Log.e("error","Time is not valid");
//
//            }
//            else{binding.errorTime.setVisibility(View.GONE);}
//
//            if(isDateValid && isTimeValid) {
//
//                //convert string to localDate & LocalTime
//                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                LocalTime recordTime = LocalTime.parse(recordTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
//
//                if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
//
//                    //blood pressure need 2 measurement values
//                    if (!TextUtils.isEmpty(recordMeasure2)) {
//                        binding.errorGeneral.setVisibility(View.GONE);
//
//                        // initialize vital object with current date and time
//                        Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
//                        newVital.setMeasurement1(recordMeasure1);
//                        newVital.setMeasurement2(recordMeasure2);
//
//                        //create new vital
//                        vitalViewModel.AddVital(newVital);
//
//                    } else {
//                        // At least one field is empty or null
//                        binding.errorGeneral.setText("One or more fields are empty.");
//                        binding.errorGeneral.setVisibility(View.VISIBLE); // Make the TextView visible
//
//                        System.out.println("One or more fields are empty.");
//                        Log.d("Activity", "One or more fields are empty.");
//                    }
//
//                } else if (VitalsCategories.isValidVitalCategory(vitalType)) {
//                    binding.errorGeneral.setVisibility(View.GONE);
//
//                    // initialize vital object with current date and time
//                    Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
//                    newVital.setMeasurement1(recordMeasure1);
//
//                    //create new vital
//                    vitalViewModel.AddVital(newVital);
//
//                } else {
//                    System.out.println("could not define the vital type");
//                    Log.d("Activity", "could not define the vital type");
//                }
//            }
//        } else {
//
//            // At least one field is empty or null
//            binding.errorGeneral.setText("One or more fields are empty.");
//            binding.errorGeneral.setVisibility(View.VISIBLE); // Make the TextView visible
//
//            System.out.println("One or more fields are empty.");
//            Log.d("Activity", "One or more fields are empty.");
//        }
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
            showError(binding.errorDate, "Invalid Date (e.g., 15-05-2025)", !isDateValid);

            // Handle time error
            showError(binding.errorTime, "Invalid Time (e.g., 14:30)", !isTimeValid);

            if (isDateValid && isTimeValid) {
                LocalDate recordDate = LocalDate.parse(recordDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalTime recordTime = LocalTime.parse(recordTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

                if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
                    if (!TextUtils.isEmpty(recordMeasure2)) {

                        // Initialize and save Vital object
                        Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
                        newVital.setMeasurement1(recordMeasure1);
                        newVital.setMeasurement2(recordMeasure2);

                        vitalViewModel.AddVital(newVital);
                    } else {
                        // Handle missing field error
                        showError(binding.errorGeneral, "One or more fields are empty.", true);
                        Log.e("error", "One or more fields are empty.");
                    }
                } else if (VitalsCategories.isValidVitalCategory(vitalType)) {
                    showError(binding.errorGeneral, "", false);

                    Vital newVital = new Vital(recordDate, recordTime, vitalType, unit);
                    newVital.setMeasurement1(recordMeasure1);

                    vitalViewModel.AddVital(newVital);
                } else {
                    Log.d("Activity", "Could not define the vital type");
                }
            }
        } else {
            //Handle missing field error
            showError(binding.errorGeneral, "One or more fields are empty.", true);
            Log.e("error", "One or more fields are empty.");
        }


    }

    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }
}