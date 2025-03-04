package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMainBinding;

import com.example.insight.model.Symptom;
import com.example.insight.utility.SymptomsCategories;
import com.example.insight.viewmodel.SymptomViewModel;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends DrawerBaseActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private SymptomViewModel symptomViewModel;
    List<Symptom> symptomsList = new ArrayList<>();
    Symptom symptom = new Symptom();
    Button btn, manageAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Symptoms");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }
        btn = binding.button;
        manageAccount = binding.manageAccount;

        manageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageAccount.class));
            }
        });

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
                AddSymptom(newSymptom1);// add newSymptom1 to Firestore

                Symptom newSymptom2 = new Symptom(symptomsCategory, "moderate","Headache with dizziness"); // initialize symptom object with current date and time
                AddSymptom(newSymptom2);// add newSymptom2 to Firestore

                // Create a cough symptom with a custom date and time
                LocalDate customDate = LocalDate.of(2024, 1, 15);
                LocalTime customStart = LocalTime.of(12, 30);
                LocalTime customEnd = LocalTime.of(13, 15);

                Symptom newSymptom3 = new Symptom(customDate, customStart, customEnd,symptomsCategory, "moderate","Headache with dizziness");
                //AddSymptom(newSymptom3); // add newSymptom3 to Firestore

                symptomsCategory = SymptomsCategories.COUGHING.toString();
                newSymptom3 = new Symptom(customDate, customStart, customEnd,symptomsCategory, "mild","some notes here");
                //AddSymptom(newSymptom3); // add newSymptom3 to Firestore


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
                GetSymptomsByIdResult("v2JIOFpdrgPswXCVgScW");
            }
        });

        // -------------------------------Update Symptom's notes' --------------------------------------------
        binding.btnUpdateNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                symptom.setSymptomDescription("updated notes at:"+timeStr);

                EditSymptomResult(symptom);
            }
        });

        // -------------------------------Get Symptoms By Id --------------------------------------------
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String symptomId = "6es7qyBgw6UshG9hIOwn";
                GetSymptomsByIdResult(symptomId);
                symptomViewModel=new SymptomViewModel();
                symptomViewModel.deleteSymptom(symptomId);
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

        symptomViewModel.GetSymptomsByDate(searchDate);

        // Observe favorite movies data
        symptomViewModel.getSymptomsData().observe(this, symptomsData -> {
            if (symptomsData != null || !symptomsData.isEmpty()) {
                symptomsList.clear();//to reset the current list
                symptomsList.addAll(symptomsData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "symptomList count>> " + symptomsList.size());
            }
            else {
                Log.d("MainActivity", "symptomList is null or empty.");
            }
        });
    }

    /** method to get a list of symptoms at a selected date range date1 & date2 included*/
    private void GetSymptomByDateRangeResults(LocalDate date1 ,LocalDate date2){
        symptomViewModel=new SymptomViewModel();

        // Call the search method
        symptomViewModel.GetSymptomsByDateRange(date1,date2);

        // Observe favorite movies data
        symptomViewModel.getSymptomsData().observe(this, symptomsData -> {
            if (symptomsData != null || !symptomsData.isEmpty()) {
                symptomsList.clear();//to reset the current list
                symptomsList.addAll(symptomsData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "symptomList count>> " + symptomsList.size());
            }
            else {
                Log.d("MainActivity", "symptomList is null or empty.");
            }
        });

    }

    /** method to get a list of symptoms for the selected type*/
    private void GetSymptomsByTypeResults(String symptomType){
        symptomViewModel=new SymptomViewModel();

        symptomViewModel.GetSymptomsByType(symptomType);

        // Observe favorite movies data
        symptomViewModel.getSymptomsData().observe(this, symptomsData -> {
            if (symptomsData != null || !symptomsData.isEmpty()) {
                symptomsList.clear();//to reset the current list
                symptomsList.addAll(symptomsData);
                //symptomsListAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "symptomList count>> " + symptomsList.size());
            }
            else {
                Log.d("MainActivity", "symptomList is null or empty.");
            }
        });

    }

    /** method to get a symptom by ID*/
    private void GetSymptomsByIdResult(String symptomId){
        symptomViewModel=new SymptomViewModel();

        symptomViewModel.GetSymptomById(symptomId);

        // Observe favorite movies data
        symptomViewModel.getSelectedSymptomData().observe(this, selectedSymptomData -> {
            symptom = new Symptom();//to reset the current list
            if (selectedSymptomData != null) {
                symptom =selectedSymptomData;
                Log.d("MainActivity", "symptomId: " + symptom.getSymptomId()+" Name: "+ symptom.getSymptomName());
                Log.d("MainActivity", "Description: " + symptom.getSymptomDescription());
            }
            else {
                Log.d("MainActivity", "symptom is null");

            }
        });

    }

    private void EditSymptomResult(Symptom updatedSymptom){
        //symptomViewModel=new SymptomViewModel();

        symptomViewModel.UpdateSymptom(updatedSymptom);

        // Observe favorite movies data
        symptomViewModel.getSelectedSymptomData().observe(this, selectedSymptomData -> {

            if (selectedSymptomData != null) {
                symptom =selectedSymptomData;
                Log.d("MainActivity", "symptomId: " + symptom.getSymptomId()+" Name: "+ symptom.getSymptomName());
                Log.d("MainActivity", "updated Description: " + symptom.getSymptomDescription());
            }
            else {
                Log.d("MainActivity", "symptom is null");
            }
        });

    }



}