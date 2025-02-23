package com.example.insight.view;

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

import com.example.insight.adapter.VitalsListAdapter;
import com.example.insight.databinding.FragmentVitalsListBinding;
import com.example.insight.model.Vital;
import com.example.insight.utility.DateValidator;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VitalsListFragment extends Fragment implements ItemClickListener {
    private FragmentVitalsListBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private VitalViewModel viewModel;
    private List<Vital> vitalList;
    private VitalsListAdapter vitalsListAdapter;
    private String vitalType;
    LocalDate searchDate;
    String searchDateStr;
    Bundle bundle;
    String searchCriteria;
    String title;
    String image;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentVitalsListBinding.inflate(inflater, container, false);
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

        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(VitalViewModel.class);
        //by default show all vital by type
        //viewModel.GetVitalsByType(vitalType);
        searchCriteria = "type";

        // Setup RecyclerView
        vitalsListAdapter = new VitalsListAdapter(requireContext(), new ArrayList<>()); // Initialize with null data
        vitalsListAdapter.setClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(vitalsListAdapter);

        // Observe Vitals Data
        viewModel.getVitalsData().observe(getViewLifecycleOwner(), vitalsData -> {
            Log.d("debug", "--Update View at vitals list recycler view--");
            vitalList = vitalsData;
            vitalsListAdapter.updateData(vitalsData); // Update adapter data

            // Hide loading indicator
            binding.progressBar.setVisibility(View.GONE);//----it does not work for now
        });

        // Observe Search Message
        viewModel.getSearchResultMessageData().observe(getViewLifecycleOwner(), searchResultMessageData -> {
            binding.searchMessage.setText(searchResultMessageData);
        });


        // Search Button Logic
        binding.iconSearch.setOnClickListener(v -> {
            searchDateStr = binding.searchTextDate.getText().toString();

            if (searchDateStr.isEmpty()){
                //if the the search date is empty it will get all dates for this vital type
                Log.i("trace", "Search initiated for: " + searchDateStr);
                viewModel.GetVitalsByType(vitalType);
                searchCriteria = "type";
            }else {
                //if there is a date inserted, validate it before sending the request
                boolean isDateValid = DateValidator.isValidDate(searchDateStr);
                if (isDateValid){
                    viewModel.GetVitalsByDateAndType(searchDateStr, vitalType);
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

//        if (!vitalType.isEmpty() && vitalType != null){
//            viewModel.GetVitalsByType(vitalType);
//        }
        viewModel.GetVitalsByType(vitalType);
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
        if (vitalList != null && pos < vitalList.size()) {
            String vitalID = vitalList.get(pos).getVitalId();

            //navigate to vitalDetail Page
            Intent intentObj = new Intent(requireContext(), VitalDetails.class);
            intentObj.putExtra("vitalID", vitalID);
            intentObj.putExtra("vitalType","");
            intentObj.putExtra("unit","");
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
        } else if (pos >= 0 && pos < vitalList.size()) {
            String uid = user.getUid();
            String vitalID = vitalList.get(pos).getVitalId();
            viewModel.DeleteVital(vitalID); // ---------------Delete vital from Firestore DB
            //Toast.makeText(getContext(), "Vital record is successfully deleted", Toast.LENGTH_LONG).show();

            //To refresh the recyclerview list
            if (searchCriteria.equals("date")){
                viewModel.GetVitalsByDate(searchDateStr);
            }else{
                viewModel.GetVitalsByType(vitalType);

            }
        }

    }
}