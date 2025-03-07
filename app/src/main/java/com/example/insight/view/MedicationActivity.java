package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivityVitalsBinding;
import com.example.insight.model.Vital;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationActivity extends DrawerBaseActivity {

    ActivityVitalsBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    private VitalsListFragment vitalsListFragment;
    private VitalGraphFragment vitalGraphFragment;
    private VitalViewModel vitalViewModel;
    List<Vital> vitalsList = new ArrayList<>();
    Vital vital = new Vital();
    String vitalType;
    String unit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVitalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Vitals");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(MedicationActivity.this, Login.class));
        }

        //get the vital type from the intent
        Intent intentObject = getIntent();
        vitalType = intentObject.getStringExtra("vitalType");
        unit = intentObject.getStringExtra("unit");
        String title = intentObject.getStringExtra("title");
        String image = intentObject.getStringExtra("image");

        // Initialize ViewModel
        vitalViewModel = new ViewModelProvider(this).get(VitalViewModel.class);

        //get the vitals for the current month and current year
        vitalViewModel.GetVitalsByMonthAndType(LocalDate.now().getMonthValue(),LocalDate.now().getYear(),vitalType);
        vitalViewModel.GetVitalsByType(vitalType);

        //set the image and the title of the page according to the vital type
        binding.textViewTitle.setText(title);
        String imageName = image;
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);

        // Create an instance of the fragment
        vitalsListFragment = new VitalsListFragment();
        vitalGraphFragment = new VitalGraphFragment();

        // Create a Bundle to hold the data
        Bundle bundle = new Bundle();
        bundle.putString("vitalType", vitalType); // Add data to the Bundle
        bundle.putString("title", title); // Add title to the Bundle
        bundle.putString("image", image); // Add image to the Bundle
        bundle.putString("unit", unit); // Add image to the Bundle

        // Set the Bundle as arguments for the fragment
        vitalsListFragment.setArguments(bundle);
        vitalGraphFragment.setArguments(bundle);

        //replaceFragment(vitalsListFragment); // Show vitals list by default
        replaceFragment(vitalGraphFragment); // Show vitals list by default

        binding.btnListByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(vitalsListFragment);
            }
        });
        binding.btnTrends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(vitalGraphFragment);
            }
        });

        // -------------------------------Add Button --------------------------------------------
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //navigate to Add vital page
                Intent intentObj = new Intent(getApplicationContext(), VitalDetails.class);
                intentObj.putExtra("vitalID", "");
                intentObj.putExtra("vitalType",vitalType);
                intentObj.putExtra("unit",unit);
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