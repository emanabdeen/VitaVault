package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityLoginBinding;
import com.example.insight.databinding.ActivityMainBinding;
import com.example.insight.databinding.ActivityManageAccountBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class ManageAccount extends AppCompatActivity {

    ActivityManageAccountBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button changePassButton;
    ImageButton backButton;
    EditText oldPw, newPw, confirmPw, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageAccountBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(ManageAccount.this, Login.class));
        }
        oldPw = binding.editTextOldPw;
        newPw = binding.editTextNewPw;
        confirmPw = binding.editTextConfirmPw;
        email = binding.editTextEmail;
        backButton = binding.backButton;

        changePassButton = binding.testPasswordChange;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                if (user != null) {
                    LoginRegisterHelper lrh = new LoginRegisterHelper();
                    try {
                        lrh.changePassword(oldPw.getText().toString(), newPw.getText().toString(), confirmPw.getText().toString(), mAuth)
                                        .whenComplete((result, error) -> {
                                            if (error != null) {
                                                Log.e("MainActivity", "changePassword() call encountered an error: " + error.getMessage());
                                            }
                                            if (result == null) {
                                                Log.e("ManageAccount", "Change password result was null...");
                                                result = "An error occurred, please try again or contact support.";
                                            }
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                        });
                    } catch (InterruptedException e) {
                        Log.e("MainActivity", "Change password encountered an error: " + e.getMessage());
                    }
                }
            }
        });
    }
}