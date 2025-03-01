package com.example.insight.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.insight.databinding.ActivityIngredientsScanBinding;
import com.example.insight.utility.OcrApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IngredientsScan extends AppCompatActivity {
    private static final String TAG = "IngredientsScan";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ActivityIngredientsScanBinding binding;
    private Button cameraButton, ocrTestButton;
    private ImageView cameraResultImageView;
    private TextView ocrResultText;
    private Uri imageUri;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> launcherCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsScanBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        ocrResultText = binding.ocrResultText;
        ocrResultText.setMovementMethod(new ScrollingMovementMethod());
        launcherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Log.d(TAG, "onActivityResult: called.");
                Log.d(TAG, "onActivityResult: requestCode: " + " == " + REQUEST_IMAGE_CAPTURE + " resultCode: " + o.getResultCode() + " == " + RESULT_OK);
                if (o.getResultCode() == RESULT_OK) {
                    Bitmap[] imageBitmap = new Bitmap[1];
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                        imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                        new Thread(() -> {
                            Log.d(TAG, "Running OcrApiClient.detectText");
                            String textResult = OcrApiClient.detectText(getApplicationContext(), imageBitmap[0]);
                            Log.d(TAG, "onClick: OCR result: " + textResult);
                            runOnUiThread(() -> {
                                ocrResultText.setText(textResult);
                            });
                        }).start();
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: " + e.getMessage());
                    }
                    if (imageBitmap[0] != null) {
                        cameraResultImageView.setImageBitmap(imageBitmap[0]);
                    }
                }
            }
        });

        cameraResultImageView = binding.imgViewCameraResult;
        cameraButton = binding.cameraButton;
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        ocrTestButton = binding.ocrTestButton;
        ocrTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    Log.d(TAG, "onClick: OCR TEST - media picker launched");

                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(TAG, "dispatchTakePictureIntent: " + photoFile.getAbsolutePath());
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                imageUri = photoURI;
                currentPhotoPath = photoURI.toString();
                Log.d(TAG, "current photo path before saving: " + currentPhotoPath);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                launcherCamera.launch(takePictureIntent);
            }
            else {
                Log.e(TAG, "dispatchTakePictureIntent: failed to generate file uri");
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //String imageFileName = "JPEGtest";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (storageDir.mkdirs()){
                Log.d(TAG, "createImageFile: directory created successfully");
            } else {
                Log.d(TAG, "createImageFile: failed to create directory " + storageDir.getName());
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
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


    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    try {
                        Log.d("PhotoPicker", "Selected URI: " + uri.toString());
                        Log.d("PhotoPicker", "Launching Detect Text call");

                        Bitmap[] imageBitmap = new Bitmap[1];
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                            imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                            new Thread(() -> {
                                Log.d(TAG, "Running OcrApiClient.detectText");
                                String textResult = OcrApiClient.detectText(getApplicationContext(), imageBitmap[0]);
                                Log.d(TAG, "onClick: OCR result: " + textResult);
                                runOnUiThread(() -> {
                                    ocrResultText.setText(textResult);
                                });
                            }).start();
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                        }
                        if (imageBitmap[0] != null) {
                            cameraResultImageView.setImageBitmap(imageBitmap[0]);
                        }
                    } catch (Exception e) {
                        Log.e("PhotoPicker", "Exception when picking picture: " + e.getMessage());
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
}