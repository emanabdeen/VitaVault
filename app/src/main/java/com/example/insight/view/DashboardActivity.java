package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBarDrawerToggle;

import com.example.insight.R;
import com.example.insight.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends DrawerBaseActivity {

    ActivityDashboardBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dashboard");

        //// Disable the back icon for this activity
        //setShowBackIcon(false);

        // Hide the back button for this activity
        ImageButton backButton = toolbar.findViewById(R.id.back_button);
        backButton.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DashboardActivity.this, Login.class));
        }

        //---------------go to vitals----------------------
        binding.cardVitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, VitalsMainActivity.class));
            }
        });

        binding.cardSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, SymptomsMainActivity.class));
            }
        });
        binding.cardMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, MedicationsActivity.class));
            }
        });
        binding.cardOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, OCRMainActivity.class));
            }
        });

        binding.cardDietry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, DietaryRestrictionsMainActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}