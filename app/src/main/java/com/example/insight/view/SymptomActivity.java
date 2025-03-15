package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivitySymptomBinding;
import com.example.insight.model.Symptom;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SymptomActivity extends DrawerBaseActivity {

    ActivitySymptomBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    private SymptomsListFragment symptomsListFragment;
    private SymptomViewModel viewModel;
    List<Symptom> symptomsList = new ArrayList<>();
    String symptomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Symptoms");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(SymptomActivity.this, Login.class));
        }

        //get the Symptom type from the intent
        Intent intentObject = getIntent();
        symptomType = intentObject.getStringExtra("symptomType");
        String title = intentObject.getStringExtra("title");
        String image = intentObject.getStringExtra("image");

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SymptomViewModel.class);

        //set the image and the title of the page according to the Symptom type
        binding.textViewTitle.setText(title);
        String imageName = image;
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);

        // Create an instance of the fragment
        symptomsListFragment = new SymptomsListFragment();

        // Create a Bundle to hold the data
        Bundle bundle = new Bundle();
        bundle.putString("symptomType", symptomType); // Add data to the Bundle
        bundle.putString("title", title); // Add title to the Bundle
        bundle.putString("image", image); // Add image to the Bundle

        // Set the Bundle as arguments for the fragment
        symptomsListFragment.setArguments(bundle);

        replaceFragment(symptomsListFragment); // Show vitals list by default

        /*binding.btnListByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(symptomsListFragment);
            }
        });*/

        // -------------------------------Add Button --------------------------------------------
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //navigate to Add vital page
                Intent intentObj = new Intent(getApplicationContext(), SymptomDetails.class);
                intentObj.putExtra("symptomID", "");
                intentObj.putExtra("symptomType",symptomType);
                intentObj.putExtra("title", title);
                intentObj.putExtra("image", image);
                startActivity(intentObj);
            }
        });



    }
    private void replaceFragment (Fragment fragment){
        // Get the FragmentManager
        FragmentManager fm = getSupportFragmentManager();

        // Begin a FragmentTransaction
        FragmentTransaction ft = fm.beginTransaction();

        // Replace the fragment in the container (R.id.fragmentLayout)
        ft.replace(R.id.fragmentLayout, fragment);

        // Add the transaction to the back stack
        //ft.addToBackStack(null);

        // Commit the transaction
        ft.commit();
    }
}