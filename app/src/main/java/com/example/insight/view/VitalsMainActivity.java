package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.insight.viewmodel.VitalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class VitalsMainActivity extends AppCompatActivity {
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
        this.setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(VitalsMainActivity.this, Login.class));
        }

        Toolbar toolbar=binding.toolbar;
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sittings){
            Toast.makeText(this,"Setting is clicked",Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.prefrences){
            Toast.makeText(this,"prefrences is clicked",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}