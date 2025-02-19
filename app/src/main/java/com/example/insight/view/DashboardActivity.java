package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.insight.R;
import com.example.insight.databinding.ActivityDashboardBinding;
import com.example.insight.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(DashboardActivity.this, Login.class));
        }
        //-------------------Toolbar-----------

        Toolbar toolbar=binding.toolbarLayout.toolbar;
        setSupportActionBar(toolbar);

        //-------------------------------------logout button ----------------------------
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                if (user != null) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(DashboardActivity.this, Login.class));
                }else{
                    Toast.makeText(getApplicationContext(), "You aren't logged in yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //---------------go to vitals----------------------
        binding.btnGoToVitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, VitalsMainActivity.class));
            }
        });

        binding.btnGoToSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            }
        });
    }
}