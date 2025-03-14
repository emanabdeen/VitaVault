package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMyProfileBinding;
import com.example.insight.model.UserAccount;
import com.example.insight.viewmodel.AccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

public class MyProfileActivity extends DrawerBaseActivity {

    ActivityMyProfileBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    AccountViewModel viewModel;
    UserAccount userAccount;
    private static final String TAG = "Activity"; // Tag for logging
    String age;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("My Account");

        //inst ViewModel that handles fetching the symptoms details
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        binding.txtEmail.setText(user.getEmail());
        viewModel.GetUserProfile();

        // Populate age spinner
        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.age_array,
                android.R.layout.simple_spinner_item
        );
        ageAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        binding.spinnerAge.setAdapter(ageAdapter);

        // Populate gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        binding.spinnerGender.setAdapter(genderAdapter);


        //to observe the data and display it
        viewModel.getUserAccountData().observe(this, userAccountData -> {
            userAccount = userAccountData;

            if (userAccount != null) {
                // Set the retrieved values in UI
                gender = userAccount.getGender(); // Get gender from UserAccount
                age = userAccount.getAgeRange();  // Get age from UserAccount

                // Set gender spinner selection
                if (gender != null && !gender.isEmpty()) {
                    int genderPosition = getPositionForSpinner(binding.spinnerGender, gender);
                    if (genderPosition != -1) {
                        binding.spinnerGender.setSelection(genderPosition);
                    }
                }else{
                    // Set a default selection (select option) first item
                    binding.spinnerGender.setSelection(0);
                }

                // Set age spinner selection
                if (age != null && !age.isEmpty()) {
                    int agePosition = getPositionForSpinner(binding.spinnerAge, age);
                    if (agePosition != -1) {
                        binding.spinnerAge.setSelection(agePosition);
                    }
                }else{
                    // Set a default selection (select option) first item
                    binding.spinnerAge.setSelection(0);
                }
            }

        });

        // Handle Spinner item selection
        binding.spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString().toLowerCase(Locale.ROOT);
                if (!s.equals("select option")) {
                    gender = parent.getItemAtPosition(position).toString();
                }else{
                    gender="";
                }
                Log.d(TAG, "Selected: " + gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle Spinner item selection
        binding.spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString().toLowerCase(Locale.ROOT);
                if (!s.equals("select option")) {
                    age = parent.getItemAtPosition(position).toString();
                }else{
                    age="";
                }
                Log.d(TAG, "Selected: " + age);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, ManageAccount.class));
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount updatedUserAccount= new UserAccount();

                // Update user data in Firestore
                if (gender != null && age != null) {
                    updatedUserAccount.setGender(gender);
                    updatedUserAccount.setAgeRange(age);
                    viewModel.UpdateUserData(updatedUserAccount)
                            .thenAccept(success -> {
                                if (success) {
                                    Log.d(TAG, "User data updated successfully!");
                                    Toast.makeText(MyProfileActivity.this,"User data updated successfully!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    // Optionally, show a success message to the user
                                } else {
                                    Log.e("MyProfileActivity", "Failed to update user data.");
                                    Toast.makeText(MyProfileActivity.this,"Failed to update user data. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


    }

    /**
     * Helper method to find the position of a value in a spinner's adapter.
     */
    private int getPositionForSpinner(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return -1; // Value not found
    }
}