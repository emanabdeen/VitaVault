package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityLoginBinding;
import com.example.insight.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btn, ocrTestButton;

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
        ocrTestButton = binding.ocrTest;
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
        ocrTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), IngredientsScan.class);
                startActivity(intentObj);
            }
        });
    }
}