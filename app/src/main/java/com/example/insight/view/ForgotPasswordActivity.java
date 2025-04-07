package com.example.insight.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.insight.R;
import com.example.insight.databinding.ActivityForgotPasswordBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth mAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        toolbar = binding.getRoot().findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton backButton = toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        binding.btnRequestReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = binding.editTxtEmail.getText().toString();
                if (LoginRegisterHelper.validateEmail(email)) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.successMessage.setVisibility(View.VISIBLE);
                                binding.pageTitle.setVisibility(View.GONE);
                            }
                            else {
                                Log.e("ForgotPasswordActivity", "Error sending password reset email: " + task.getException().getMessage());
                                binding.txtErrorMessage.setText(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }
}