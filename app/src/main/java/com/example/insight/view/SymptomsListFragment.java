package com.example.insight.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.insight.adapter.SymptomsListAdapter;
import com.example.insight.databinding.FragmentSymptomsListBinding;
import com.example.insight.model.Symptom;
import com.example.insight.utility.DateValidator;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SymptomsListFragment extends Fragment implements ItemClickListener{

    private FragmentSymptomsListBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private SymptomViewModel viewModel;
    private List<Symptom> symptomList;
    private SymptomsListAdapter symptomsListAdapter;
    private String symptomType;
    LocalDate searchDate;
    String searchDateStr;
    Bundle bundle;
    String searchCriteria;
    String title;
    String image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentSymptomsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Retrieve the Bundle
        bundle = getArguments();
        if (bundle != null) {
            // Get the data from the Bundle
            symptomType = bundle.getString("symptomType");
            title = bundle.getString("title");
            image = bundle.getString("image");

        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SymptomViewModel.class);
        //by default show all by type
        //viewModel.GetSymptomsByType(symptomType);
        searchCriteria = "type";

        // Setup RecyclerView
        symptomsListAdapter = new SymptomsListAdapter(requireContext(), new ArrayList<>()); // Initialize with null data
        symptomsListAdapter.setClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(symptomsListAdapter);

        // Observe Symptoms Data
        viewModel.getSymptomsData().observe(getViewLifecycleOwner(), symptomsData -> {
            Log.d("debug", "--Update View at symptoms list recycler view--");

            if(symptomsData != null && !symptomsData.isEmpty()){
                symptomList = symptomsData;
                symptomsListAdapter.updateData(symptomsData); // Update adapter data
                binding.recyclerLayout.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }

        });

        // Observe Search Message
        viewModel.getSearchResultMessageData().observe(getViewLifecycleOwner(), searchResultMessageData -> {

            if(searchResultMessageData != null && !searchResultMessageData.isEmpty()){
                binding.searchMessage.setText(searchResultMessageData);
                binding.searchMessage.setVisibility(View.VISIBLE);
                binding.recyclerLayout.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            }
            /*binding.searchMessage.setText(searchResultMessageData);*/
        });

        // Set up date pickers for start date inputs
        binding.searchTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.searchTextDate);
            }
        });

        //if search by date cleared get all symptoms by type
        binding.btnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchTextDate.setText("");
                viewModel.GetSymptomsByType(symptomType);
                searchCriteria = "type";
            }
        });


        // Search Button Logic
        binding.iconSearch.setOnClickListener(v -> {
            searchDateStr = binding.searchTextDate.getText().toString();

            if (!searchDateStr.isEmpty()){
                //if there is a date inserted, validate it before sending the request
                boolean isDateValid = DateValidator.isValidDate(searchDateStr);
                if (isDateValid){
                    viewModel.GetSymptomsByDateAndType(searchDateStr, symptomType);
                    searchCriteria = "date";
                }else{
                    Toast.makeText(requireContext(), "Please enter a valid date", Toast.LENGTH_LONG).show();
                }
            }else{
                viewModel.GetSymptomsByType(symptomType);
                searchCriteria = "type";
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.GetSymptomsByType(symptomType);
        searchCriteria = "type";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear binding to avoid memory leaks
        binding = null;
    }

    @Override
    public void OnClickItem(View v, int pos) {
        if (symptomList != null && pos < symptomList.size()) {
            String symptomID = symptomList.get(pos).getSymptomId();

            //navigate to symptomDetail Page
            Intent intentObj = new Intent(requireContext(), SymptomDetails.class);
            intentObj.putExtra("symptomID", symptomID);
            intentObj.putExtra("symptom","");
            intentObj.putExtra("title", title);
            intentObj.putExtra("image", image);
            startActivity(intentObj);
        }

    }

    @Override
    public void OnClickDelete(View v, int pos) {
        if (user == null) {
            Intent intentObj = new Intent(requireContext(), Login.class);
            startActivity(intentObj);
            Log.d("tag", "Redirect user to login page");
        } else if (pos >= 0 && pos < symptomList.size()) {
            String uid = user.getUid();
            String symptomID = symptomList.get(pos).getSymptomId();
            viewModel.DeleteSymptom(symptomID); // ---------------Delete from Firestore DB
            //Toast.makeText(getContext(), "symptom record is successfully deleted", Toast.LENGTH_LONG).show();

            //To refresh the recyclerview list
            if (searchCriteria.equals("date")){
                viewModel.GetSymptomsByDate(searchDateStr);
            }else{
                viewModel.GetSymptomsByType(symptomType);

            }
        }

    }

    private void showDatePicker(TextInputEditText dateInput) {
        // Get current date
        Calendar calendar = Calendar.getInstance();

        // Create constraints for date selection (optional)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Create MaterialDatePicker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(calendar.getTimeInMillis()) // Set current date as default
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert milliseconds to Calendar
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selection);

            // Format the selected date (YYYY-MM-DD)
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH) + 1; // +1 because months are 0-based
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH)+1;

            String selectedDate = String.format("%04d-%02d-%02d", year, month, day);
            dateInput.setText(selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }
}