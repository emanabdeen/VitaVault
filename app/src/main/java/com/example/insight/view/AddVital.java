package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        //set the image and the title of the page according to the vital type
        String imageName = intentObject.getStringExtra("image");
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);


        //put initial values for current time and date
        binding.editTextDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        binding.editTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        //if the vital is not blood pressure, hide measure2 and put the measurement label = unit
        if (!Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
            binding.editTextMeasure2.setVisibility(View.GONE);
            binding.lblMeasure2.setVisibility(View.GONE);
            binding.lblMeasure1.setText(unit);
        }
        //if the vital is blood pressure, hide measure2 and put the measurement labels
        //if the vital is blood pressure, hide measure2 and put the measurement labels Systolic ,Diastolic
        if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
            binding.lblMeasure1.setText("Systolic " + unit);
            binding.lblMeasure2.setText("Diastolic " + unit);
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

                try {
                    CreateNewVital();
                    finish();
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

                            //add the record to firestore
                            vitalViewModel.AddVital(newVital);
                            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();

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

    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }


}