package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityLoginBinding;
import com.example.insight.databinding.ActivityMainBinding;
import com.example.insight.model.CoughingSymptom;
import com.example.insight.model.NauseaSymptom;
import com.example.insight.model.OtherSymptom;
import com.example.insight.model.Symptom;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btn;
    private SymptomViewModel symptomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        btn = binding.button;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                if (user != null) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, Login.class));
                }else{
                    Toast.makeText(getApplicationContext(), "You aren't logged in yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //-------------------------------


        // Search Button Logic
        binding.btnSymptom.setOnClickListener(v -> {
            symptomViewModel=new SymptomViewModel();

            // Create symptoms using default recordDate and current time
            CoughingSymptom cough = new CoughingSymptom("moderate");
            NauseaSymptom nausea = new NauseaSymptom("severe");
            OtherSymptom other = new OtherSymptom("moderate","Headache with dizziness");

            // Create a cough symptom with a custom date and time
            LocalDate customDate = LocalDate.of(2024, 1, 15);
            LocalTime customStart = LocalTime.of(8, 30); // 08:30 AM
            LocalTime customEnd = LocalTime.of(9, 15); // 09:15 AM
            CoughingSymptom oldCough = new CoughingSymptom(customDate, customStart, customEnd, "mild");
            OtherSymptom oldOther = new OtherSymptom(customDate, customStart, customEnd,"moderate","Headache with dizziness");


            // Print formatted output
            //System.out.println("Cough Record Date: " + cough.getRecordDate());
            //Log.d("debug","Cough Record Date: " + cough.getRecordDate());

            //System.out.println("Cough Start Time: " + cough.getStartTime());
            //Log.d("debug","Cough Start Time: " + cough.getStartTime());

            //System.out.println("Cough End Time: " + cough.getEndTime());
            //Log.d("debug","Cough End Time: " + cough.getEndTime());

            //System.out.println("Old Cough Record Date: " + oldCough.getRecordDate());
            //Log.d("debug","Old Cough Record Date: " + oldCough.getRecordDate());

            //System.out.println("Old Cough Start Time: " + oldCough.getStartTime());
            //Log.d("debug","Old Cough Start Time: " + oldCough.getStartTime());

            //System.out.println("Old Cough End Time: " + oldCough.getEndTime());
            //Log.d("debug","Old Cough End Time: " + oldCough.getEndTime());


            //System.out.println("Nausea Record Date: " + nausea.getRecordDate());
            //Log.d("debug","Nausea Record Date: " + nausea.getRecordDate());

            //System.out.println("Other Symptom Description: " + other.getDescription());
            //Log.d("debug","Other Symptom Description: " + other.getDescription());

            //---------------------------------------------

            String uid = user.getUid(); // Get the logged-in user's unique ID

            //add new symptoms-----------------------------
            symptomViewModel.AddSymptom(uid, oldCough);
            symptomViewModel.AddSymptom(uid, nausea);
            symptomViewModel.AddSymptom(uid, other);
            symptomViewModel.AddSymptom(uid, oldOther);

            // End of onClick button
        });

        // Search Button Logic
        binding.btnGetData.setOnClickListener(v -> {
            symptomViewModel=new SymptomViewModel();

            String uid = user.getUid(); // Get the logged-in user's unique ID

            //retrieve symptom by date
            LocalDate searchDate = LocalDate.now(); // Example: today's date
            LocalDate searchDate2 = LocalDate.of(2024, 1, 15);

            // Call the search method
            symptomViewModel.searchSymptomsByDate(uid, searchDate2, new SymptomViewModel.OnSymptomsRetrievedListener() {

                @Override
                public void onSymptomsRetrieved(List<Symptom> symptoms) {

                    Log.d("MainActivity", "symptomList count>> " + symptoms.size());
                    if (symptoms != null && !symptoms.isEmpty()) {
                        for (Symptom symptom : symptoms) {
                            Log.d("MainActivity", "Retrieved Symptom: " + symptom.toString());

                            Log.d("MainActivity", "Symptom name: " + symptom.getSymptomName());
                            Log.d("MainActivity", "Symptom level: " + symptom.getSymptomLevel());
                            Log.d("MainActivity", "Symptom Description: " + symptom.getSymptomDescription());
                            Log.d("MainActivity", "Symptom start time: " + symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                            Log.d("MainActivity", "Symptom end time: " + symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                            Log.d("MainActivity", "Symptom date: " + symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        }
                    } else {
                        Log.d("MainActivity", "No symptoms found for the selected date.");
                    }
                }
            });

            // End of onClick button
        });
    }
}