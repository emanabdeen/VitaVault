package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.insight.databinding.ActivityDietaryRestrictionsMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DietaryRestrictionsMainActivity extends DrawerBaseActivity {

    ActivityDietaryRestrictionsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDietaryRestrictionsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Dietary Restrictions");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DietaryRestrictionsMainActivity.this, Login.class));
        }


        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), DietaryRestrictionsAddCustomActivity.class);
                startActivity(intentObj);
            }
        });

    }
}