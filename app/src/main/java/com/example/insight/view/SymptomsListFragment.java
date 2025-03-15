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
            symptomList = symptomsData;
            symptomsListAdapter.updateData(symptomsData); // Update adapter data

            // Hide loading indicator
            binding.progressBar.setVisibility(View.GONE);//----it does not work for now
        });

        // Observe Search Message
        viewModel.getSearchResultMessageData().observe(getViewLifecycleOwner(), searchResultMessageData -> {
            binding.searchMessage.setText(searchResultMessageData);
        });

        // Set up date pickers for start date inputs
        binding.searchTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.searchTextDate);
            }
        });


        // Search Button Logic
        binding.iconSearch.setOnClickListener(v -> {
            searchDateStr = binding.searchTextDate.getText().toString();

            if (searchDateStr.isEmpty()){
                //if the the search date is empty it will get all dates for this symptom type
                viewModel.GetSymptomsByType(symptomType);
                searchCriteria = "type";
            }else {
                //if there is a date inserted, validate it before sending the request
                boolean isDateValid = DateValidator.isValidDate(searchDateStr);
                if (isDateValid){
                    viewModel.GetSymptomsByDateAndType(searchDateStr, symptomType);
                    searchCriteria = "date";
                }else{
                    Toast.makeText(requireContext(), "Please enter a valid date", Toast.LENGTH_LONG).show();
                }

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
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it to the input field
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}