package com.example.insight.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.insight.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private boolean showBackIcon = true; // Default to showing the back icon

    @Override
    public void setContentView(View view) {
        drawerLayout =(DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null) ;
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);
        //set up the toolbar
        toolbar =drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        // Enable the back icon in the Toolbar (built in toolbar)
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the back icon
//            getSupportActionBar().setDisplayShowHomeEnabled(true); // Ensure the icon is clickable
//        }

        // Find the back button in the custom Toolbar
        ImageButton backButton = toolbar.findViewById(R.id.back_button);

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up the NavigationView

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open, R.string.close);


        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);//-----------
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this,DashboardActivity.class));
            overridePendingTransition(0,0);
        } else if (id == R.id.nav_symptoms) {
            startActivity(new Intent(this,MainActivity.class));
            overridePendingTransition(0,0);
        } else if (id == R.id.nav_vitals) {
            startActivity(new Intent(this,VitalsMainActivity.class));
            overridePendingTransition(0,0);
        } else if (id == R.id.nav_reports) {

            // Handle Reports navigation
        } else if (id == R.id.nav_dietary) {
            // Handle Dietary Restrictions navigation
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this,ManageAccount.class));
            overridePendingTransition(0,0);
        }
        else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(DrawerBaseActivity.this, Login.class));
        }
        drawerLayout.closeDrawers(); // Close drawer after selection
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back icon click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Call the back button behavior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // to set the title in the action bar
    protected void allocateActivityTitle(String titleString){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle((titleString));

        }
    }

    // Method to control the visibility of the back icon
    public void setShowBackIcon(boolean showBackIcon) {
        this.showBackIcon = showBackIcon;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackIcon);
            getSupportActionBar().setDisplayShowHomeEnabled(showBackIcon);
        }
    }
}