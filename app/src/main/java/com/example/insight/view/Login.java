package com.example.insight.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.insight.databinding.ActivityLoginBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        //Navigate to Registration page
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), Register.class);
                startActivity(intentObj);
            }
        });

        //signIn
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate Email & PW are not empty
                String userEmail = binding.editTxtEmail.getText().toString();
                String userPW = binding.editTxtPW.getText().toString();
                //String userEmail= "test@test.com";
                //String userPW= "Test@123";

                if (userEmail.matches("") || userPW.matches("")) {
                    //show error message
                    binding.txtErrorMessage.setText("Please insert a valid email and a valid password.");
                } else {
                    signIn(binding.editTxtEmail.getText().toString(), binding.editTxtPW.getText().toString());
                    //signIn("test@test.com", "Test@123");
                }
            }
        });

        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intentObj);
            }
        });

        Intent intentObj = getIntent();
        TextView title = binding.pageTitle;
        ConstraintLayout successMessage = binding.successMessage;

        //Read Intent value. if registration process succeeded, will show success message
        boolean registerSuccess = intentObj.getBooleanExtra("registerSuccess", false);
        // Set visibility based on registerSuccess
        if (registerSuccess) {
            title.setVisibility(View.GONE);
            successMessage.setVisibility(View.VISIBLE);
        } else {
            successMessage.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if success, update the UI with the signed-in user's info
                            Log.d("tag", "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser(); //initialize user object and assign the the value
                            Intent intentObj = new Intent(getApplicationContext(), IntroActivity.class); //Navigate to page
                            startActivity(intentObj);
                            finish();
                        } else {
                            // If sign-in fails, check the exception for the reason
                            Exception exception = task.getException();
                            if (exception != null) {
                                if (exception instanceof FirebaseAuthInvalidUserException) {
                                    // Email does not exist
                                    Log.e("tag", "signInWithEmail:failure - Email not found");
                                    //show error message
                                    binding.txtErrorMessage.setText("This email is not registered. Please register.");
                                    //updateUI(null); // this is a method
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid email-password combination
                                    Log.e("tag", "signInWithEmail:failure - Incorrect password");
                                    //show error message
                                    binding.txtErrorMessage.setText("Incorrect email or password. Please try again.");
                                    //updateUI(null); // this is a method
                                } else {
                                    // General failure
                                    Log.e("tag", "signInWithEmail:failure - " + exception.getMessage());
                                    //show error message
                                    binding.txtErrorMessage.setText(exception.getMessage());
                                    //updateUI(null); // this is a method
                                }
                            } else {
                                // No exception details
                                Log.e("tag", "signInWithEmail:failure - Unknown error");
                                //show error message
                                binding.txtErrorMessage.setText("Sorry something went wrong :(");
                                //updateUI(null); // this is a method
                            }
                        }
                    }
                });
    }
}