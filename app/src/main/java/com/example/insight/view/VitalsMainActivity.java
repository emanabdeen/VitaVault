package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.insight.R;
import com.example.insight.databinding.ActivityVitalsBinding;
import com.example.insight.databinding.ActivityVitalsMainBinding;
import com.example.insight.model.Vital;
import com.example.insight.utility.VitalsCategories;
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class VitalsMainActivity extends DrawerBaseActivity {
    ActivityVitalsMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private VitalViewModel vitalViewModel;
    List<Vital> vitalsList = new ArrayList<>();
    Vital vital = new Vital();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVitalsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Vitals");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(VitalsMainActivity.this, Login.class));
        }

        binding.cardTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObj = new Intent(getApplicationContext(), VitalsActivity.class);
                intentObj.putExtra("vitalType",VitalsCategories.BodyTemperature.toString());// register status to the second page
                startActivity(intentObj);
            }
        });


    }

}