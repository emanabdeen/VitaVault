package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityLoginBinding;
import com.example.insight.databinding.ActivityMainBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btn, changePassButton;
    EditText oldPw, newPw, confirmPw, email;

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
        oldPw = binding.editTextOldPw;
        newPw = binding.editTextNewPw;
        confirmPw = binding.editTextConfirmPw;
        email = binding.editTextEmail;

        btn = binding.button;
        changePassButton = binding.testPasswordChange;

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
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                if (user != null) {
                    LoginRegisterHelper lrh = new LoginRegisterHelper();
                    try {
                        CompletableFuture<String> changePasswordResult = lrh.changePassword(oldPw.getText().toString(), newPw.getText().toString(), confirmPw.getText().toString(), mAuth);
                        changePasswordResult.thenAccept( result -> {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        });

                    } catch (InterruptedException e) {
                        Log.e("MainActivity", "Change password encountered an error: " + e.getMessage());
                    }
                }
            }
        });
    }
}