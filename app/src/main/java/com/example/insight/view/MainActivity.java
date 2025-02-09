package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityMainBinding;

import com.example.insight.model.Symptom;
import com.example.insight.utility.SymptomsCategories;
import com.example.insight.viewmodel.SymptomViewModel;
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

        // -------------------------------Add Symptom Button --------------------------------------------
        binding.btnSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String symptomsCategory = SymptomsCategories.HEADACHE.toString();

                Symptom newSymptom1 = new Symptom(symptomsCategory, "moderate"); // initialize symptom object with current date and time
                //AddSymptom(newSymptom1);// add newSymptom1 to Firestore

                Symptom newSymptom2 = new Symptom(symptomsCategory, "moderate","Headache with dizziness"); // initialize symptom object with current date and time
                //AddSymptom(newSymptom2);// add newSymptom2 to Firestore

                // Create a cough symptom with a custom date and time
                LocalDate customDate = LocalDate.of(2024, 1, 15);
                LocalTime customStart = LocalTime.of(12, 30);
                LocalTime customEnd = LocalTime.of(13, 15);

                Symptom newSymptom3 = new Symptom(customDate, customStart, customEnd,symptomsCategory, "moderate","Headache with dizziness");
                //AddSymptom(newSymptom3); // add newSymptom3 to Firestore

                symptomsCategory = SymptomsCategories.COUGHING.toString();
                newSymptom3 = new Symptom(customDate, customStart, customEnd,symptomsCategory, "mild","some notes here");
                AddSymptom(newSymptom3); // add newSymptom3 to Firestore


            }
        });

        // -------------------------------Get Symptoms By Date --------------------------------------------
        binding.btnGetSymptomsByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LocalDate searchDate = LocalDate.now(); // Example: today's date
                LocalDate searchDate = LocalDate.of(2024, 1, 15);

                GetSymptomByDateResults(searchDate);
            }
        });


        // -------------------------------Get Symptoms In dates range --------------------------------------------
        binding.btnGetSymptomsInRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LocalDate searchDate = LocalDate.now(); // Example: today's date
                LocalDate date1 = LocalDate.of(2025, 2, 1);
                LocalDate date2 = LocalDate .now();

                GetSymptomByDateRangeResults(date1,date2);
            }
        });

        // -------------------------------Get Symptoms By Type --------------------------------------------
        binding.btnGetSymptomsByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String symptomType = SymptomsCategories.COUGHING.toString();
                GetSymptomsByTypeResults(symptomType);
            }
        });

        // -------------------------------Get Symptoms By Id --------------------------------------------
        binding.btnGetSymptomById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String symptomId = "hCsmk7jnYBnvG8qKInp4";
                GetSymptomsByIdResult(symptomId);
            }
        });

        // -------------------------------Update Symptom's notes' --------------------------------------------
        binding.btnUpdateNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String symptomId = "hCsmk7jnYBnvG8qKInp4";
                GetSymptomsByIdResult(symptomId);
            }
        });

    }

    /** method to add a new symptom to the system. required parameters that will be collected from the page as follow:
     * - LocalDate date: the input filed will have current date as default "dd-MM-yyyy"(can be updated by user)
     * - LocalTime start time: input filed will have current time HH:mm as default (can be updated by user)
     * - LocalTime end time: input filed will have current time HH:mm as default (can be updated by user)
     * - String level: (REQUIRED) user will select one of these choices (Not present, Severe, Moderate, ...etc)
     * - string description: (optional) user can add more notes about this symptom
     * - string symptomName: this will be automatically assigned by the class that will be used to create the symptom object*/
    private void AddSymptom(Symptom symptom){
        symptomViewModel=new SymptomViewModel();
        String uid = user.getUid(); // Get the logged-in user's unique ID

        symptomViewModel.AddSymptom(uid, symptom);
    }

    /** method to get a list of symptoms at a selected date*/
    private void GetSymptomByDateResults(LocalDate searchDate){
        symptomViewModel=new SymptomViewModel();

        String uid = user.getUid(); // Get the logged-in user's unique ID

        // Call the search method
        //symptomViewModel.searchSymptomsByDate(uid, searchDate);

        symptomViewModel.GetSymptomsByDate(uid, searchDate, new SymptomViewModel.OnSymptomsListRetrievedListener() {

            @Override
            public void onSymptomsRetrieved(List<Symptom> symptoms) {

                Log.d("MainActivity", "----------------------GetSymptomsByDate--------------------------------" );
                Log.d("MainActivity", "symptomList count>> " + symptoms.size()); // number of symptoms in the list

                if (symptoms != null && !symptoms.isEmpty()) {
                    for (Symptom symptom : symptoms) {
                        // the properties for each symptom in the list
                        String symptomName = symptom.getSymptomName();
                        String symptomLevel = symptom.getSymptomLevel();
                        String symptomDescription = symptom.getSymptomDescription();
                        String symptomStartTime = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomEndTime = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomDate = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        Log.d("MainActivity", "Retrieved Symptom: " + symptom.toString());
                        Log.d("MainActivity", "Symptom name: " + symptomName);
                        Log.d("MainActivity", "Symptom level: " + symptomLevel);
                        Log.d("MainActivity", "Symptom Description: " + symptomDescription);
                        Log.d("MainActivity", "Symptom start time: " + symptomStartTime);
                        Log.d("MainActivity", "Symptom end time: " + symptomEndTime);
                        Log.d("MainActivity", "Symptom date: " + symptomDate);
                        Log.d("MainActivity", "Symptom ID: " + symptom.getSymptomId());
                        Log.d("MainActivity", "------------------------------------------------------" );
                    }
                } else {
                    Log.d("MainActivity", "No symptoms found for the selected date.");
                }
            }
        });

    }

    /** method to get a list of symptoms at a selected date range date1 & date2 included*/
    private void GetSymptomByDateRangeResults(LocalDate date1 ,LocalDate date2){
        symptomViewModel=new SymptomViewModel();

        String uid = user.getUid(); // Get the logged-in user's unique ID

        // Call the search method
        //symptomViewModel.searchSymptomsByDate(uid, searchDate);

        symptomViewModel.GetSymptomsByDateRange(uid, date1,date2, new SymptomViewModel.OnSymptomsListRetrievedListener() {

            @Override
            public void onSymptomsRetrieved(List<Symptom> symptoms) {

                Log.d("MainActivity", "----------------------GetSymptomsByDateRange--------------------------------" );
                Log.d("MainActivity", "symptomList count>> " + symptoms.size()); // number of symptoms in the list

                if (symptoms != null && !symptoms.isEmpty()) {
                    for (Symptom symptom : symptoms) {
                        // the properties for each symptom in the list
                        String symptomName = symptom.getSymptomName();
                        String symptomLevel = symptom.getSymptomLevel();
                        String symptomDescription = symptom.getSymptomDescription();
                        String symptomStartTime = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomEndTime = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomDate = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        Log.d("MainActivity", "Retrieved Symptom: " + symptom.toString());
                        Log.d("MainActivity", "Symptom name: " + symptomName);
                        Log.d("MainActivity", "Symptom level: " + symptomLevel);
                        Log.d("MainActivity", "Symptom Description: " + symptomDescription);
                        Log.d("MainActivity", "Symptom start time: " + symptomStartTime);
                        Log.d("MainActivity", "Symptom end time: " + symptomEndTime);
                        Log.d("MainActivity", "Symptom date: " + symptomDate);
                        Log.d("MainActivity", "Symptom ID: " + symptom.getSymptomId());
                        Log.d("MainActivity", "------------------------------------------------------" );
                    }
                } else {
                    Log.d("MainActivity", "No symptoms found in the selected date range.");
                }
            }
        });

    }

    /** method to get a list of symptoms for the selected type*/
    private void GetSymptomsByTypeResults(String symptomType){
        symptomViewModel=new SymptomViewModel();

        String uid = user.getUid(); // Get the logged-in user's unique ID


        symptomViewModel.GetSymptomsByType(uid, symptomType, new SymptomViewModel.OnSymptomsListRetrievedListener() {

            @Override
            public void onSymptomsRetrieved(List<Symptom> symptoms) {

                Log.d("MainActivity", "----------------------GetSymptomsByType--------------------------------" );
                Log.d("MainActivity", "symptomList count>> " + symptoms.size()); // number of symptoms in the list

                if (symptoms != null && !symptoms.isEmpty()) {
                    for (Symptom symptom : symptoms) {
                        // the properties for each symptom in the list
                        String symptomName = symptom.getSymptomName();
                        String symptomLevel = symptom.getSymptomLevel();
                        String symptomDescription = symptom.getSymptomDescription();
                        String symptomStartTime = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomEndTime = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomDate = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        Log.d("MainActivity", "Retrieved Symptom: " + symptom.toString());
                        Log.d("MainActivity", "Symptom name: " + symptomName);
                        Log.d("MainActivity", "Symptom level: " + symptomLevel);
                        Log.d("MainActivity", "Symptom Description: " + symptomDescription);
                        Log.d("MainActivity", "Symptom start time: " + symptomStartTime);
                        Log.d("MainActivity", "Symptom end time: " + symptomEndTime);
                        Log.d("MainActivity", "Symptom date: " + symptomDate);
                        Log.d("MainActivity", "Symptom ID: " + symptom.getSymptomId());
                        Log.d("MainActivity", "------------------------------------------------------" );
                    }
                } else {
                    Log.d("MainActivity", "No symptoms found for the selected date.");
                }
            }
        });

    }

    /** method to get a symptom by ID*/
    private void GetSymptomsByIdResult(String symptomId){
        symptomViewModel=new SymptomViewModel();

        String uid = user.getUid(); // Get the logged-in user's unique ID

        //symptomViewModel.UpdateSymptom(uid, symptomId,  updatedSymptom);
        symptomViewModel.GetSymptomById(uid, symptomId, new SymptomViewModel.OnSymptomObjectRetrievedListener() {

            @Override
            public void onSymptomRetrieved(Symptom symptom) {

                if (symptom != null) {
                        // the properties for each symptom in the list
                        String symptomName = symptom.getSymptomName();
                        String symptomLevel = symptom.getSymptomLevel();
                        String symptomDescription = symptom.getSymptomDescription();
                        String symptomStartTime = symptom.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomEndTime = symptom.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        String symptomDate = symptom.getRecordDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        Log.d("MainActivity", "----------------------GetSymptomById--------------------------------" );
                        Log.d("MainActivity", "Retrieved Symptom: " + symptom.toString());
                        Log.d("MainActivity", "Symptom name: " + symptomName);
                        Log.d("MainActivity", "Symptom level: " + symptomLevel);
                        Log.d("MainActivity", "Symptom Description: " + symptomDescription);
                        Log.d("MainActivity", "Symptom start time: " + symptomStartTime);
                        Log.d("MainActivity", "Symptom end time: " + symptomEndTime);
                        Log.d("MainActivity", "Symptom date: " + symptomDate);
                        Log.d("MainActivity", "Symptom ID: " + symptom.getSymptomId());
                        Log.d("MainActivity", "------------------------------------------------------" );

                } else {
                    Log.d("MainActivity", "No symptoms found for the selected ID.");
                }
            }
        });
    }

}