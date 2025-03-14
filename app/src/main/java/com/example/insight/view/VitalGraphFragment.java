package com.example.insight.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.insight.databinding.FragmentVitalGraphBinding;
import com.example.insight.model.VitalRecord;
import com.example.insight.utility.ChartHelper;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.utility.YearMonthHelper;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class VitalGraphFragment extends Fragment {

    private FragmentVitalGraphBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private VitalViewModel viewModel;
    private String vitalType;
    Bundle bundle;
    private List<VitalRecord> vitalRecordsList ; // Add a list to hold your vital data
    String title;
    String image;
    String unit;
    YearMonth currentYearMonth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentVitalGraphBinding.inflate(inflater, container, false);
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
            vitalType = bundle.getString("vitalType");
            title = bundle.getString("title");
            image = bundle.getString("image");
            unit = bundle.getString("unit");
        }


        //if the vital is blood pressure, hide measure2 and put the measurement labels Systolic ,Diastolic
        final String measureName1; // Make final
        final String measureName2; // Make final

        if (Objects.equals(vitalType, VitalsCategories.BloodPressure.toString())) {
            measureName1 = "Systolic";
            measureName2 = "Diastolic";
        } else {
            measureName1 = ""; // Initialize to empty strings if not BloodPressure
            measureName2 = "";
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(VitalViewModel.class);

        //set YearMonth value ... default current
        currentYearMonth = YearMonth.now();
        setYearMonthTitle ();

        //get the vitals for the current month and current year
        viewModel.GetVitalsByMonthAndType(LocalDate.now().getMonthValue(),LocalDate.now().getYear(),vitalType);

        // Observe Vitals Data
        viewModel.getVitalRecordsData().observe(getViewLifecycleOwner(), vitalRecordsData -> {
            vitalRecordsList = vitalRecordsData;
            if (vitalRecordsList != null && !vitalRecordsList.isEmpty()) { // Null check
                ChartHelper.createLineChart(view, vitalRecordsList, requireContext(), measureName1, measureName2);
                binding.graphLayoutParent.setVisibility(View.VISIBLE);
                binding.unitAxisLable.setText(unit);
                //binding.dayAxisLable.setText("Day");

            }else{
                binding.graphLayoutParent.setVisibility(View.INVISIBLE);
            }
        });

        // Observe Search Message
        viewModel.getSearchResultMessageData().observe(getViewLifecycleOwner(), searchResultMessageData -> {
            binding.message.setText(searchResultMessageData);
        });


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentYearMonth=YearMonthHelper.getPreviousYearMonthData(currentYearMonth);
                setYearMonthTitle ();

                int month = currentYearMonth.getMonthValue();
                int year = currentYearMonth.getYear();
                viewModel.GetVitalsByMonthAndType(month,year,vitalType);
            }
        });

        binding.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentYearMonth=YearMonthHelper.getNextYearMonthData(currentYearMonth);
                setYearMonthTitle ();
                int month = currentYearMonth.getMonthValue();
                int year = currentYearMonth.getYear();
                viewModel.GetVitalsByMonthAndType(month,year,vitalType);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        //get the vitals for the current month and current year
        viewModel.GetVitalsByMonthAndType(LocalDate.now().getMonthValue(),LocalDate.now().getYear(),vitalType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setYearMonthTitle (){
        String monthName = YearMonthHelper.getLocalizedMonthName(currentYearMonth);
        String year = String.valueOf(currentYearMonth.getYear());
        binding.txtMonth.setText(monthName +"\n"+year);

    }
}