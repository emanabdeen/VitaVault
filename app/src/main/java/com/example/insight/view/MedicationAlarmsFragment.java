package com.example.insight.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.insight.adapter.MedicationAlarmAdapter;
import com.example.insight.databinding.FragmentMedicationAlarmsBinding;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.viewmodel.MedicationAlarmsViewModel;
import com.example.insight.viewmodel.MedicationViewModel;

import java.util.ArrayList;

public class MedicationAlarmsFragment extends Fragment {

    private String medicationId;
    private FragmentMedicationAlarmsBinding binding;
    private MedicationAlarmAdapter adapter;
    private MedicationAlarmsViewModel viewModel;
    private String medicationName;
    private String dosage;
    private MedicationViewModel medicationViewModel;
    private ActivityResultLauncher<Intent> alarmActivityLauncher;

    public static MedicationAlarmsFragment newInstance(String medicationId) {
        MedicationAlarmsFragment fragment = new MedicationAlarmsFragment();
        Bundle args = new Bundle();
        args.putString("medicationID", medicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicationId = getArguments().getString("medicationID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicationAlarmsBinding.inflate(inflater, container, false);
        setupRecyclerView();

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);

        //Set this up so a fetch from database only happens if user actually updated something
        alarmActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        viewModel.forceRefreshAlarms(medicationId); // Only fetch if something changed
                    }
                }
        );

        viewModel.getAlarms().observe(getViewLifecycleOwner(), alarms -> {
            if (alarms != null && !alarms.isEmpty()) {
                binding.textViewNoAlarms.setVisibility(View.GONE);
                binding.recyclerViewAlarms.setVisibility(View.VISIBLE);
                adapter.updateAlarms(alarms);
            } else {
                binding.textViewNoAlarms.setVisibility(View.VISIBLE);
                binding.recyclerViewAlarms.setVisibility(View.GONE);
            }
        });

        // Fetch alarms for the medication
        viewModel.fetchAlarms(medicationId);

        // Initialize MedicationViewModel to fetch medication details.
        medicationViewModel = new ViewModelProvider(this).get(MedicationViewModel.class);
        medicationViewModel.getMedication(medicationId).observe(getViewLifecycleOwner(), medication -> {
            if (medication != null) {
                medicationName = medication.getName();
                dosage = medication.getDosage() +  " " + medication.getUnit();
            }
        });

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Set up the adapter with the EditItemClickListener callbacks.
        adapter = new MedicationAlarmAdapter(requireContext(), new ArrayList<>(), new EditItemClickListener() {
            @Override
            public void OnClickEdit(View v, int pos) {
                // Retrieve the alarm being edited from the adapter.
                MedicationAlarm alarm = adapter.getAlarmAt(pos);
                if (alarm != null) {
                    Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
                    // Pass alarm details for editing
                    intent.putExtra("alarmId", alarm.getAlarmId());
                    intent.putExtra("medicationId", alarm.getMedicationId());
                    intent.putExtra("day", alarm.getDay());
                    intent.putExtra("time", alarm.getTime());
                    intent.putExtra("medicationName", medicationName);
                    intent.putExtra("dosage", dosage);
                    // Optionally, pass additional data if needed.
                    alarmActivityLauncher.launch(intent);

                } else {
                    Toast.makeText(getContext(), "Unable to retrieve alarm for editing.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnClickItem(View v, int pos) {
                // Retrieve the alarm being edited from the adapter.
                MedicationAlarm alarm = adapter.getAlarmAt(pos);
                if (alarm != null) {
                    Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
                    // Pass alarm details for editing
                    intent.putExtra("alarmId", alarm.getAlarmId());
                    intent.putExtra("medicationId", alarm.getMedicationId());
                    intent.putExtra("day", alarm.getDay());
                    intent.putExtra("time", alarm.getTime());
                    intent.putExtra("medicationName", medicationName);
                    intent.putExtra("dosage", dosage);
                    // Optionally, pass additional data if needed.
                    alarmActivityLauncher.launch(intent);

                } else {
                    Toast.makeText(getContext(), "Unable to retrieve alarm for editing.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnClickDelete(View v, int pos) {
                // Retrieve the alarm to delete
                MedicationAlarm alarm = adapter.getAlarmAt(pos);
                if (alarm != null) {
                    int requestCode = (alarm.getMedicationId() + alarm.getDay() + alarm.getTime()).hashCode();
                    // Cancel the alarm using AlarmHelper
                    AlarmHelper.cancelAlarm(getContext(), requestCode, alarm.getMedicationId(), "MedicationName");
                    // Delete the alarm from Firestore via the ViewModel.
                    viewModel.deleteAlarm(alarm.getMedicationId(), alarm.getAlarmId());

                    // Remove the alarm from local storage.
                    com.example.insight.utility.AlarmLocalStorageHelper.removeAlarm(getContext(), alarm);
                } else {
                    Toast.makeText(getContext(), "Unable to retrieve alarm for deletion.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.recyclerViewAlarms.setAdapter(adapter);
        binding.recyclerViewAlarms.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Only reloads if alarms aren't already up to date
        // For example, user just added a new alarm
        viewModel.fetchAlarms(medicationId);
    }

    public void refreshAlarms() {
        if (viewModel != null && medicationId != null) {
            viewModel.forceRefreshAlarms(medicationId);
        }
    }

}
