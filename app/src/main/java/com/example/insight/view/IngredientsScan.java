package com.example.insight.view;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.insight.databinding.ActivityIngredientsScanBinding;

import java.io.File;

public class IngredientsScan extends AppCompatActivity {
    private static final String TAG = "IngredientsScan";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ActivityIngredientsScanBinding binding;
    private Button cameraButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsScanBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        cameraButton = binding.cameraButton;
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });
    }


    private void dispatchTakePictureIntent() {
        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "dispatchTakePictureIntent: " + e.getMessage());
            // display error state to the user
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            dispatchTakePictureIntent();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
    }


    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Log.d(TAG, "checkPermissions: permission denied");
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}