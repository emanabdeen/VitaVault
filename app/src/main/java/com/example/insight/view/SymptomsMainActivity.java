package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.databinding.ActivitySymptomsMainBinding;
import com.example.insight.model.Symptom;
import com.example.insight.utility.SymptomsCategories;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SymptomsMainActivity extends DrawerBaseActivity {

    ActivitySymptomsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private SymptomViewModel viewModel;
    List<Symptom> symptomsList = new ArrayList<>();
    //Symptom symptom = new Symptom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Symptoms");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(SymptomsMainActivity.this, Login.class));
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SymptomViewModel.class);

        binding.cardHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.HEADACHE.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.HEADACHE.toString());
                intentObj.putExtra("title", "Headache");
                intentObj.putExtra("image", "@drawable/ic_headache");
                startActivity(intentObj);
            }
        });

        binding.cardChestPain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.CHEST_PAIN.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.CHEST_PAIN.toString());
                intentObj.putExtra("title", "Chest pain");
                intentObj.putExtra("image", "@drawable/ic_chest_pain");
                startActivity(intentObj);
            }
        });
        binding.cardCoughing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.COUGHING.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.COUGHING.toString());
                intentObj.putExtra("title", "Coughing");
                intentObj.putExtra("image", "@drawable/ic_cough");
                startActivity(intentObj);
            }
        });
        binding.cardJointPain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.JOIN_PAIN.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.JOIN_PAIN.toString());
                intentObj.putExtra("title", "Joint Pain");
                intentObj.putExtra("image", "@drawable/ic_joint_pain");
                startActivity(intentObj);
            }
        });
        binding.cardSoreThroat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.SORE_THROAT.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.SORE_THROAT.toString());
                intentObj.putExtra("title", "Sore Throat");
                intentObj.putExtra("image", "@drawable/ic_sore_throat");
                startActivity(intentObj);
            }
        });
        binding.cardRash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.RASH.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.RASH.toString());
                intentObj.putExtra("title", "Rash");
                intentObj.putExtra("image", "@drawable/ic_rash");
                startActivity(intentObj);
            }
        });
        binding.cardOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), SymptomActivity.class);
                viewModel.GetSymptomsByType(SymptomsCategories.OTHER.toString());
                intentObj.putExtra("symptomType",SymptomsCategories.OTHER.toString());
                intentObj.putExtra("title", "Other");
                intentObj.putExtra("image", "@drawable/ic_symptoms_others");
                startActivity(intentObj);
            }
        });
    }
}