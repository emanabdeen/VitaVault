package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMainBinding;
import com.example.insight.databinding.ActivityVitalsBinding;
import com.example.insight.model.Symptom;
import com.example.insight.model.Vital;
import com.example.insight.utility.SymptomsCategories;
import com.example.insight.utility.Unites;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.SymptomViewModel;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VitalsActivity extends AppCompatActivity {

    ActivityVitalsBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private VitalViewModel vitalViewModel;
    List<Vital> vitalsList = new ArrayList<>();
    Vital vital = new Vital();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVitalsBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(VitalsActivity.this, Login.class));
        }


        // -------------------------------Add Symptom Button --------------------------------------------
        binding.btnVital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VitalsCategories vitalType = VitalsCategories.BodyTemperature;
                String measure = "37";

                Vital newVital1 = new Vital(vitalType.toString(), Unites.Celsius.toString()); // initialize vital object with current date and time
                newVital1.setMeasurement1(measure);
                AddVital(newVital1);// add newVital1 to Firestore

                Vital newVital2 = new Vital(VitalsCategories.BloodPressure.toString(), Unites.mmHg.toString()); // initialize vital object with current date and time
                newVital2.setMeasurement1("120");
                newVital2.setMeasurement2("80");
                AddVital(newVital2);// add newVital1 to Firestore

                // Create
                LocalDate customDate = LocalDate.of(2025, 2, 8);
                LocalTime customtime = LocalTime.of(20, 30);

                Vital newVital3 = new Vital(customDate, customtime,VitalsCategories.Weight.toString(),Unites.Kilograms.toString()); // initialize vital object with current date and time
                newVital3.setMeasurement1("78.5");
                AddVital(newVital3);// add newVital1 to Firestore

            }
        });

        // -------------------------------Get Symptoms By Date --------------------------------------------
        binding.btnGetVitalssByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LocalDate searchDate = LocalDate.now(); // Example: today's date
                LocalDate searchDate = LocalDate.of(2025, 2, 8);

                GetVitalByDateResults(searchDate);
            }
        });

        // -------------------------------Get Symptoms By Type --------------------------------------------
        binding.btnGetVitalsByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String vitalType = VitalsCategories.BloodPressure.toString();
                GetVitalssByTypeResults(vitalType);
            }
        });

    }

    private void AddVital(Vital vital){
        vitalViewModel=new VitalViewModel();
        String uid = user.getUid(); // Get the logged-in user's unique ID

        vitalViewModel.AddVital(vital);
    }

    /** method to get a list of vitals at a selected date*/
    private void GetVitalByDateResults(LocalDate searchDate){
        vitalViewModel=new VitalViewModel();

        vitalViewModel.GetVitalsByDate(searchDate);

        // Observe favorite movies data
        vitalViewModel.getVitalsData().observe(this, vitalsData -> {
            if (vitalsData != null || !vitalsData.isEmpty()) {
                vitalsList.clear();//to reset the current list
                vitalsList.addAll(vitalsData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "vitalsList count>> " + vitalsList.size());
            }
            else {
                Log.d("MainActivity", "vitalsList is null or empty.");
            }
        });
    }

    /** method to get a list of vitals for the selected type*/
    private void GetVitalssByTypeResults(String vitalType){
        vitalViewModel=new VitalViewModel();

        vitalViewModel.GetVitalsByType(vitalType);

        // Observe favorite movies data
        vitalViewModel.getVitalsData().observe(this, vitalsData -> {
            if (vitalsData != null || !vitalsData.isEmpty()) {
                vitalsList.clear();//to reset the current list
                vitalsList.addAll(vitalsData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "symptomList count>> " + vitalsList.size());
            }
            else {
                Log.d("MainActivity", "symptomList is null or empty.");
            }
        });

    }
}