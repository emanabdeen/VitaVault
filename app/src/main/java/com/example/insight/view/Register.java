package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.insight.databinding.ActivityRegisterBinding;
import com.example.insight.utility.LoginRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        //Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Register button
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate Email & PW are not empty
                String userEmail= binding.editTxtEmail.getText().toString();
                String userPW= binding.editTxtPW.getText().toString();
                if(userEmail.matches("")||userPW.matches("")){
                    //show error message
                    //password was null
                    binding.txtErrorMessage.setText("Please insert a valid email and a valid password.");
                    //show error message
                    //password was not complex enough
                    binding.txtErrorMessage.setText("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
                }
                else{
                    registerUser(binding.editTxtEmail.getText().toString(), binding.editTxtPW.getText().toString());
                }
            }
        });
    }



    private void registerUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            //if success, update the UI with the signed-in user's info
                            Log.d("tag", "createUserWithEmailAndPassword:success");
                            //Toast.makeText(Register.this, "Register User passed.", Toast.LENGTH_SHORT).show();

                            //Navigate to login page
                            Intent intentObj = new Intent(getApplicationContext(), Login.class);
                            intentObj.putExtra("registerSuccess",true);// register status to the second page
                            startActivity(intentObj);
                            finish();

                            //updateUI(user); // this is a method

                        }else{

                            // If registration fails, check the exception for the reason
                            Exception exception = task.getException();
                            if (exception != null) {
                                if (exception instanceof FirebaseAuthUserCollisionException) {
                                    // Email already exists
                                    Log.e("tag", "createUserWithEmailAndPassword:failure - Email already exists");
                                    //show error message
                                    binding.txtErrorMessage.setText("Email is already in use. Please use a different email.");
                                    //updateUI(null); // this is a method
                                } else {
                                    // General failure
                                    Log.e("tag", "createUserWithEmailAndPassword:failure - " + exception.getMessage());
                                    binding.txtErrorMessage.setText(exception.getMessage());
                                    //updateUI(null); // this is a method
                                }
                            } else {
                                // No exception information
                                binding.txtErrorMessage.setText("Sorry something went wrong :(");
                                //updateUI(null); // this is a method
                            }
                        }
                    }
                });
    }
}