package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insight.R;
import com.example.insight.databinding.ActivityManageAccountBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccount extends DrawerBaseActivity {

    ActivityManageAccountBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("My Account");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(ManageAccount.this, Login.class));
        }

        errorTextView = findViewById(R.id.errorDate);

        binding.btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPw = binding.editTextOldPw.getText().toString();
                String newPw = binding.editTextNewPw.getText().toString();
                String confirmPw = binding.editTextConfirmPw.getText().toString();

                if (validatePassword(newPw, confirmPw)) {
                    mAuth = FirebaseAuth.getInstance();
                    if (user != null) {
                        LoginRegisterHelper lrh = new LoginRegisterHelper();
                        try {
                            lrh.changePassword(oldPw, newPw, confirmPw, mAuth)
                                    .whenComplete((result, error) -> {
                                        if (error != null) {
                                            Log.e("MainActivity", "changePassword() call encountered an error: " + error.getMessage());
                                        }
                                        if (result == null) {
                                            Log.e("ManageAccount", "Change password result was null...");
                                            result = "An error occurred, please try again or contact support.";
                                        }
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                        finish();
                                    });
                        } catch (InterruptedException e) {
                            Log.e("MainActivity", "Change password encountered an error: " + e.getMessage());
                        }
                    }
                }
            }
        });
    }

    private boolean validatePassword(String newPw, String confirmPw) {
        if (newPw.length() < 8) {
            showError("Password must be at least 8 characters long.");
            return false;
        }

        if (!newPw.matches(".*[A-Z].*")) {
            showError("Password must contain at least one uppercase letter.");
            return false;
        }

        if (!newPw.matches(".*[a-z].*")) {
            showError("Password must contain at least one lowercase letter.");
            return false;
        }

        if (!newPw.matches(".*\\d.*")) {
            showError("Password must contain at least one number.");
            return false;
        }

        if (!newPw.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            showError("Password must contain at least one special character.");
            return false;
        }

        if (!newPw.equals(confirmPw)) {
            showError("Passwords do not match.");
            return false;
        }

        errorTextView.setVisibility(View.INVISIBLE);
        return true;
    }

    private void showError(String errorMessage) {
        errorTextView.setText(errorMessage);
        errorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}