package com.example.insight.view;

import static com.example.insight.utility.AlarmHelper.cancelAllAlarmsForMedication;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.insight.R;
import com.example.insight.databinding.ActivityMedicationsBinding;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.viewmodel.MedicationAlarmsViewModel;
import com.example.insight.viewmodel.MedicationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MedicationsActivity extends DrawerBaseActivity {

    private ActivityMedicationsBinding binding;
    private MedicationViewModel medicationViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MedicationsListFragment medicationsListFragment;
    private boolean medicationChanged = false;
    private static final int REQUEST_CODE_MEDICATION_DETAILS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Medications");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Redirect to login if user is not authenticated
        if (user == null) {
            finish();
            startActivity(new Intent(MedicationsActivity.this, Login.class));
            return;
        }

        // Initialize ViewModel
        medicationViewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        // Handle Deletion (alarm cancel + Firestore delete)
        medicationViewModel.getMedicationToDelete().observe(this, medication -> {
            if (medication != null) {
                // 1) Fetch the alarms from the subcollection
                MedicationAlarmsViewModel alarmsViewModel =
                        new ViewModelProvider(this).get(MedicationAlarmsViewModel.class);

                alarmsViewModel.fetchAlarmsForDeletion(medication.getMedicationId(), alarmList -> {
                    // 2) Cancel all alarms using the new helper
                    AlarmHelper.cancelAllAlarmsForMedication(
                            MedicationsActivity.this,
                            medication.getMedicationId(),
                            medication.getName(),
                            alarmList
                    );

                    // 3) Now remove the medication doc (and logs, if any)
                    medicationViewModel.removeMedication(medication.getMedicationId());
                });
            }
        });

        // Load Fragment
        medicationsListFragment = new MedicationsListFragment();
        replaceFragment(medicationsListFragment);

        // Fetch medications
        medicationViewModel.getMedications(true); // You may decide to show loading here or not

        // Add Medication Button Click Listener
        binding.btnAddMedication.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationsActivity.this, MedicationDetails.class);
            startActivityForResult(intent, REQUEST_CODE_MEDICATION_DETAILS);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (medicationChanged) {
            // Clear the filter text in the fragment and refresh data.
            if (medicationsListFragment != null) {
                medicationsListFragment.clearFilter();
                medicationViewModel.getMedications(false); // Fetch the latest data when returning
            }
            medicationChanged = false;
        } else {
            // do nothing to preserve the current filter state.
        }

    }

    /**
     * Replaces the fragment container with the provided fragment.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentLayout, fragment);
        ft.commit();
    }

    //state management to see if we should refresh list or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEDICATION_DETAILS && resultCode == RESULT_OK) {
            // Medication was added/updated, so clear the filter and refresh the list.
            // For instance, tell your fragment to clear its filter:
            if (medicationsListFragment != null) {
                medicationsListFragment.clearFilter();
            }
            medicationChanged = true;
        }
    }

}
