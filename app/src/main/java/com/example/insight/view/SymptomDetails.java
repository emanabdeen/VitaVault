package com.example.insight.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivitySymptomDetailsBinding;
import com.example.insight.databinding.ActivityVitalDetailsBinding;
import com.example.insight.model.Symptom;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.TimeValidator;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.SymptomViewModel;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

public class SymptomDetails extends DrawerBaseActivity {

    ActivitySymptomDetailsBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    SymptomViewModel viewModel;
    String pageFunction;
    String symptomId;
    String symptomType;
    Symptom symptom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Symptoms");

        //inst ViewModel that handles fetching the symptoms details
        viewModel = new ViewModelProvider(this).get(SymptomViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        //get the symptom type from the intent
        Intent intentObject = getIntent();

        //set the title of the page according to the symptom type
        binding.textViewTitle.setText(intentObject.getStringExtra("title"));

        //set the image of the page according to the symptom type
        String imageName = intentObject.getStringExtra("image");
        int imageResId = getResources().getIdentifier(imageName.replace("@drawable/", ""), "drawable", getPackageName());
        binding.image.setImageResource(imageResId);

        //if symptom has value, so this page is edit symptom
        if (!intentObject.getStringExtra("symptomID").isEmpty()) {

            pageFunction = "editSymptom";
            symptomId = intentObject.getStringExtra("symptomID");
            viewModel.GetSymptomById(symptomId);

            //to observe the data and display it
            viewModel.getSelectedSymptomData().observe(this, selectedSymptomData -> {
                symptom = selectedSymptomData; //symptom object

                //set the input fields values only when object is retrieved
                if (symptom.getRecordDate() != null && symptom.getStartTime() != null) {
                    symptomType = symptom.getSymptomName();

                    //add the retrieved symptom data into the input fields
                    binding.editTextDate.setText(DateValidator.LocalDateToString(symptom.getRecordDate()));
                    binding.editTimeStart.setText(TimeValidator.LocalTimeToString(symptom.getStartTime()));
                    binding.txtDescription.setText(selectedSymptomData.getSymptomDescription());

                    // because endTime is optional, so it may have value null. Because it is an optional object so to get it you have to use this expression symptom.getEndTime().orElse(null)
                    binding.editTimeEnd.setText(StringHandler.defaultIfNull(TimeValidator.LocalTimeToString(symptom.getEndTime().orElse(null))));


                    String symptomLevel = StringHandler.defaultIfNull(selectedSymptomData.getSymptomLevel());
                    // Get the corresponding RadioButton ID
                    int radioButtonId = getRadioButtonIdForValue(symptomLevel);
                    // Set the selected RadioButton
                    if (radioButtonId != -1) {
                        binding.radioGroupSymptomLevel.check(radioButtonId); //select the correct level
                    }
                }

            });

        } else {
            //if symptomId does not have value, so this page is create new symptom
            pageFunction = "createSymptom";
            symptomType = intentObject.getStringExtra("symptomType");

            //put initial values for current time and date
            binding.editTextDate.setText(DateValidator.LocalDateToString(LocalDate.now()));
            binding.editTimeStart.setText(TimeValidator.LocalTimeToString(LocalTime.now()));

        }

        // Set up date pickers for start and end date inputs
        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.editTextDate);
            }
        });

        // Save Button Click Listener
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validateSymptomInputs()) {
                        if (pageFunction.equals("createSymptom")) {
                            CreateNewSymptom();
                        } else if (pageFunction.equals("editSymptom")) {
                            EditSymptom();
                        }
                    }
                } catch (Exception e) {
                    Log.e("error", "try-catch error: " + e.getMessage());
                }
            }
        });
    }

    private void CreateNewSymptom() {
        try {
            Symptom newSymptom;

            // Get data from UI
            String recordDateStr = binding.editTextDate.getText().toString();
            String recordTimeStartStr = binding.editTimeStart.getText().toString();
            String recordTimeEndStr = StringHandler.defaultIfNull(binding.editTimeEnd.getText());//if it null will be saved as ""
            String symptomDescription = StringHandler.defaultIfNull(binding.txtDescription.getText());//if it null will be saved as ""
            String selectedLevelValue = getSelectedRadioButtonValue(binding.radioGroupSymptomLevel);

            LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);
            LocalTime recordTimeStart = TimeValidator.StringToLocalTime(recordTimeStartStr);

            if (recordTimeEndStr.isEmpty()) {
                newSymptom = new Symptom(recordDate, recordTimeStart, Optional.empty(), symptomType, selectedLevelValue, symptomDescription);
            } else {
                LocalTime endTime = TimeValidator.StringToLocalTime(recordTimeEndStr);
                newSymptom = new Symptom(recordDate, recordTimeStart, Optional.ofNullable(endTime), symptomType, selectedLevelValue, symptomDescription);
            }

            viewModel.AddSymptom(newSymptom);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Log.e("error", "try-catch error: " + e.getMessage());
        }
    }
    private void EditSymptom() {
        try {
            Symptom updatedSymptom;

            // Get data from UI
            String recordDateStr = binding.editTextDate.getText().toString();
            String recordTimeStartStr = binding.editTimeStart.getText().toString();
            String recordTimeEndStr = StringHandler.defaultIfNull(binding.editTimeEnd.getText()); //if it null will be saved as ""
            String symptomDescription = StringHandler.defaultIfNull(binding.txtDescription.getText()); //if it null will be saved as ""
            String selectedLevelValue = getSelectedRadioButtonValue(binding.radioGroupSymptomLevel);

            LocalDate recordDate = DateValidator.StringToLocalDate(recordDateStr);
            LocalTime recordTimeStart = TimeValidator.StringToLocalTime(recordTimeStartStr);

            if (recordTimeEndStr.isEmpty()) {
                updatedSymptom = new Symptom(recordDate, recordTimeStart, Optional.empty(), symptomType, selectedLevelValue, symptomDescription);
            } else {
                LocalTime endTime = TimeValidator.StringToLocalTime(recordTimeEndStr);
                updatedSymptom = new Symptom(recordDate, recordTimeStart, Optional.ofNullable(endTime), symptomType, selectedLevelValue, symptomDescription);
            }

            updatedSymptom.setSymptomId(symptomId);
            viewModel.UpdateSymptom(updatedSymptom);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Log.e("error", "try-catch error: " + e.getMessage());
        }
    }
    private boolean validateSymptomInputs() {
        // Get data from UI
        String recordDateStr = binding.editTextDate.getText().toString();
        String recordTimeStartStr = binding.editTimeStart.getText().toString();
        String recordTimeEndStr = StringHandler.defaultIfNull(binding.editTimeEnd.getText());
        String symptomDescription = StringHandler.defaultIfNull(binding.txtDescription.getText());
        String selectedLevelValue = getSelectedRadioButtonValue(binding.radioGroupSymptomLevel);

        // Check for empty fields
        /*if (TextUtils.isEmpty(recordDateStr) || TextUtils.isEmpty(recordTimeStartStr)) {
            showError(binding.errorGeneral, "One or more fields are empty.", true);
            return false;
        }*/
        boolean isDateAndTimeHaveValues= !TextUtils.isEmpty(recordDateStr) && !TextUtils.isEmpty(recordTimeStartStr);
        // Validate date and time
        boolean isDateValid = DateValidator.isValidDate(recordDateStr);
        boolean isTimeStartValid = TimeValidator.isValidTime(recordTimeStartStr);
        boolean isTimeEndValid = TextUtils.isEmpty(recordTimeEndStr) || (!TextUtils.isEmpty(recordTimeEndStr) && TimeValidator.isValidTime(recordTimeEndStr));

        // Validate symptom level
        boolean isLevelSelected = !TextUtils.isEmpty(selectedLevelValue);

        // Show errors if validation fails
        showError(binding.errorGeneral, "One or more fields are empty.", !isDateAndTimeHaveValues);
        showError(binding.errorDate, "Invalid Date (e.g., 2025-05-15)", !isDateValid);
        showError(binding.errorTimeStart, "Invalid Time (e.g., 14:30)", !isTimeStartValid);
        showError(binding.errorTimeEnd, "Invalid Time (e.g., 14:30)", !isTimeEndValid);
        showError(binding.errorLevel, "Please select a symptom level.", !isLevelSelected);

        // Return true only if all validations pass
        return isDateAndTimeHaveValues && isDateValid && isTimeStartValid && isTimeEndValid && isLevelSelected;
    }
    private int getRadioButtonIdForValue(String value) {
        switch (value) {
            case "Not Present":
                return R.id.radioNotPresent;
            case "Mild":
                return R.id.radioMild;
            case "Moderate":
                return R.id.radioModerate;
            case "Severe":
                return R.id.radioSevere;
            default:
                return -1; // No matching RadioButton
        }
    }
    private String getSelectedRadioButtonValue(RadioGroup radioGroup) {
        // Get the selected RadioButton
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedId);

        // Compare the selected RadioButton with the views in the binding object
        if (selectedRadioButton == binding.radioNotPresent) {
            return "Not Present";
        } else if (selectedRadioButton == binding.radioMild) {
            return "Mild";
        } else if (selectedRadioButton == binding.radioModerate) {
            return "Moderate";
        } else if (selectedRadioButton == binding.radioSevere) {
            return "Severe";
        } else {
            return ""; // No radio button is selected or unknown selection
        }
    }

    private void showDatePicker(TextInputEditText dateInput) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it to the input field
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
    private void showError(TextView errorView, String message, boolean isVisible) {
        if (isVisible) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.INVISIBLE);
        }
    }
}