package com.example.insight.view;

import static com.example.insight.utility.AlarmHelper.cancelAllAlarmsForMedication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivityMedicationsBinding;
import com.example.insight.model.Medication;
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

        // ✅ Handle Deletion (alarm cancel + Firestore delete)
        medicationViewModel.getMedicationToDelete().observe(this, medication -> {
            if (medication != null) {
                cancelAllAlarmsForMedication(this, medication);
                medicationViewModel.removeMedication(medication.getMedicationId());
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
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        medicationViewModel.getMedications(false); // Fetch the latest data when returning
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
}
