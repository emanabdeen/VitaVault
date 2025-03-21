package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivityVitalsMainBinding;
import com.example.insight.model.Vital;
import com.example.insight.utility.Units;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class VitalsMainActivity extends DrawerBaseActivity {
    ActivityVitalsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private VitalViewModel vitalViewModel;
    List<Vital> vitalsList = new ArrayList<>();
    Vital vital = new Vital();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVitalsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Vitals");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(VitalsMainActivity.this, Login.class));
        }

        // Initialize ViewModel
        vitalViewModel = new ViewModelProvider(this).get(VitalViewModel.class);

        binding.cardTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), VitalsActivity.class);
                vitalViewModel.GetVitalsByType(VitalsCategories.BodyTemperature.toString());
                intentObj.putExtra("vitalType",VitalsCategories.BodyTemperature.toString());
                intentObj.putExtra("unit", Units.Celsius.toString());
                intentObj.putExtra("title", "Body Temperature");
                intentObj.putExtra("image", "@drawable/body_temperature");
                startActivity(intentObj);
            }
        });

        binding.cardPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), VitalsActivity.class);
                vitalViewModel.GetVitalsByType(VitalsCategories.BloodPressure.toString());
                intentObj.putExtra("vitalType",VitalsCategories.BloodPressure.toString());
                intentObj.putExtra("unit", Units.mmHg.toString());
                intentObj.putExtra("title", "Blood Pressure");
                intentObj.putExtra("image", "@drawable/pressure");
                startActivity(intentObj);
            }
        });
        binding.cardHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), VitalsActivity.class);
                vitalViewModel.GetVitalsByType(VitalsCategories.HeartRate.toString());
                intentObj.putExtra("vitalType",VitalsCategories.HeartRate.toString());
                intentObj.putExtra("unit", Units.BPM.toString());
                intentObj.putExtra("title", "Heart Rate");
                intentObj.putExtra("image", "@drawable/heartbeat");
                startActivity(intentObj);
            }
        });

        binding.cardWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), VitalsActivity.class);
                vitalViewModel.GetVitalsByType(VitalsCategories.Weight.toString());
                intentObj.putExtra("vitalType",VitalsCategories.Weight.toString());
                intentObj.putExtra("unit", Units.Kilograms.toString());
                intentObj.putExtra("title", "Weight");
                intentObj.putExtra("image", "@drawable/weight_scale");
                startActivity(intentObj);
            }
        });


    }

}